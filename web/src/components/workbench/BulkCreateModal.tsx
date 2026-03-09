import { useMemo, useState } from 'react'
import { workItemApi } from '../../api/workItemApi'

type Props = {
  isOpen: boolean
  onClose: () => void
  onSuccess?: () => void
}

type ParsedItem = {
  clientId: number
  type: string
  assignee: string
  dueDate: string
  memo: string
  tags: string[]
}

type ParseResult = {
  items: ParsedItem[]
  parseErrors: string[]
}

const CSV_HEADER = 'clientId,type,assignee,dueDate,memo,tags'

export default function BulkCreateModal({ isOpen, onClose, onSuccess }: Props) {
  const [csvText, setCsvText] = useState(CSV_HEADER)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [submitResult, setSubmitResult] = useState<null | {
    savedCount: number
    skippedCount: number
    errors: string[]
  }>(null)

  const parsePreview = useMemo(() => parseCsvText(csvText), [csvText])

  if (!isOpen) return null

  const handleClose = () => {
    if (isSubmitting) return
    setSubmitResult(null)
    onClose()
  }

  const handleFillExample = () => {
    setCsvText(
      [
        CSV_HEADER,
        '1,FILING,Kim,2026-03-31,3월 신고 업무,vat|march',
        '2,BOOKKEEPING,Lee,2026-04-05,장부 기장,bookkeeping|monthly',
      ].join('\n')
    )
  }

  const handleSubmit = async () => {
    setSubmitResult(null)

    const { items, parseErrors } = parseCsvText(csvText)

    if (parseErrors.length > 0) {
      setSubmitResult({
        savedCount: 0,
        skippedCount: 0,
        errors: parseErrors,
      })
      return
    }

    if (items.length === 0) {
      setSubmitResult({
        savedCount: 0,
        skippedCount: 0,
        errors: ['등록할 데이터가 없습니다.'],
      })
      return
    }

    try {
      setIsSubmitting(true)
      const result = await workItemApi.bulkCreate(items)
      setSubmitResult(result)

      if (result.savedCount > 0 && onSuccess) {
        onSuccess()
      }
    } catch (error: any) {
      const responseData = error?.response?.data

      if (responseData?.data && typeof responseData.data === 'object') {
        const fieldErrors = Object.entries(responseData.data)
          .map(([field, message]) => `${field}: ${String(message)}`)

        setSubmitResult({
          savedCount: 0,
          skippedCount: 0,
          errors: fieldErrors.length > 0 ? fieldErrors : ['대량 등록 중 오류가 발생했습니다.'],
        })
      } else {
        setSubmitResult({
          savedCount: 0,
          skippedCount: 0,
          errors: [responseData?.message || '대량 등록 중 오류가 발생했습니다.'],
        })
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div style={backdropStyle}>
      <div style={modalStyle}>
        <div style={headerStyle}>
          <h2 style={{ margin: 0 }}>Bulk Create Work Items</h2>
          <button onClick={handleClose} disabled={isSubmitting} style={closeButtonStyle}>
            닫기
          </button>
        </div>

        <div style={sectionStyle}>
          <p style={helpTextStyle}>
            아래 형식의 CSV 텍스트를 붙여 넣으세요.
          </p>
          <code style={codeStyle}>{CSV_HEADER}</code>

          <div style={{ marginTop: 8 }}>
            <button type="button" onClick={handleFillExample} disabled={isSubmitting}>
              예시 채우기
            </button>
          </div>
        </div>

        <div style={sectionStyle}>
          <textarea
            value={csvText}
            onChange={(e) => setCsvText(e.target.value)}
            rows={14}
            style={textareaStyle}
            disabled={isSubmitting}
            placeholder={CSV_HEADER}
          />
        </div>

        <div style={sectionStyle}>
          <h3 style={sectionTitleStyle}>미리보기</h3>
          <div style={summaryBoxStyle}>
            <div>파싱된 행 수: {parsePreview.items.length}</div>
            <div>파싱 오류 수: {parsePreview.parseErrors.length}</div>
          </div>

          {parsePreview.parseErrors.length > 0 && (
            <div style={errorBoxStyle}>
              <strong>파싱 오류</strong>
              <ul style={listStyle}>
                {parsePreview.parseErrors.slice(0, 10).map((err, idx) => (
                  <li key={idx}>{err}</li>
                ))}
              </ul>
              {parsePreview.parseErrors.length > 10 && (
                <div>외 {parsePreview.parseErrors.length - 10}건</div>
              )}
            </div>
          )}

          {parsePreview.items.length > 0 && (
            <div style={previewTableWrapperStyle}>
              <table style={tableStyle}>
                <thead>
                  <tr>
                    <th style={thStyle}>clientId</th>
                    <th style={thStyle}>type</th>
                    <th style={thStyle}>assignee</th>
                    <th style={thStyle}>dueDate</th>
                    <th style={thStyle}>memo</th>
                    <th style={thStyle}>tags</th>
                  </tr>
                </thead>
                <tbody>
                  {parsePreview.items.slice(0, 5).map((item, idx) => (
                    <tr key={idx}>
                      <td style={tdStyle}>{item.clientId}</td>
                      <td style={tdStyle}>{item.type}</td>
                      <td style={tdStyle}>{item.assignee}</td>
                      <td style={tdStyle}>{item.dueDate}</td>
                      <td style={tdStyle}>{item.memo}</td>
                      <td style={tdStyle}>{item.tags.join(', ')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {parsePreview.items.length > 5 && (
                <div style={{ marginTop: 8 }}>
                  외 {parsePreview.items.length - 5}건 더 있음
                </div>
              )}
            </div>
          )}
        </div>

        {submitResult && (
          <div style={sectionStyle}>
            <h3 style={sectionTitleStyle}>실행 결과</h3>
            <div style={resultBoxStyle}>
              <div>저장 성공: {submitResult.savedCount}건</div>
              <div>건너뜀: {submitResult.skippedCount}건</div>
            </div>

            {submitResult.errors.length > 0 && (
              <div style={errorBoxStyle}>
                <strong>오류 상세</strong>
                <ul style={listStyle}>
                  {submitResult.errors.slice(0, 20).map((err, idx) => (
                    <li key={idx}>{err}</li>
                  ))}
                </ul>
                {submitResult.errors.length > 20 && (
                  <div>외 {submitResult.errors.length - 20}건</div>
                )}
              </div>
            )}
          </div>
        )}

        <div style={footerStyle}>
          <button onClick={handleClose} disabled={isSubmitting}>
            취소
          </button>
          <button onClick={handleSubmit} disabled={isSubmitting}>
            {isSubmitting ? '등록 중...' : '대량 등록'}
          </button>
        </div>
      </div>
    </div>
  )
}

function parseCsvText(text: string): ParseResult {
  const lines = text
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)

  if (lines.length === 0) {
    return {
      items: [],
      parseErrors: ['입력된 내용이 없습니다.'],
    }
  }

  const [headerLine, ...dataLines] = lines
  const normalizedHeader = headerLine.replace(/\s+/g, '')
  const expectedHeader = CSV_HEADER.replace(/\s+/g, '')

  if (normalizedHeader !== expectedHeader) {
    return {
      items: [],
      parseErrors: [
        `헤더가 올바르지 않습니다. 기대값: ${CSV_HEADER}`,
      ],
    }
  }

  const items: ParsedItem[] = []
  const parseErrors: string[] = []

  dataLines.forEach((line, index) => {
    const rowNumber = index + 2
    const cols = splitCsvLine(line)

    if (cols.length < 6) {
      parseErrors.push(`row ${rowNumber}: 컬럼 수가 부족합니다.`)
      return
    }

    const [clientIdRaw, typeRaw, assigneeRaw, dueDateRaw, memoRaw, tagsRaw] = cols

    const clientId = Number(clientIdRaw)
    if (!Number.isInteger(clientId) || clientId <= 0) {
      parseErrors.push(`row ${rowNumber}: clientId는 1 이상의 정수여야 합니다.`)
      return
    }

    const type = (typeRaw || '').trim()
    if (!type) {
      parseErrors.push(`row ${rowNumber}: type은 필수입니다.`)
      return
    }

    const dueDate = (dueDateRaw || '').trim()
    if (dueDate && !/^\d{4}-\d{2}-\d{2}$/.test(dueDate)) {
      parseErrors.push(`row ${rowNumber}: dueDate는 YYYY-MM-DD 형식이어야 합니다.`)
      return
    }

    const tags = (tagsRaw || '')
      .split('|')
      .map((v) => v.trim())
      .filter(Boolean)

    items.push({
      clientId,
      type,
      assignee: (assigneeRaw || '').trim(),
      dueDate,
      memo: (memoRaw || '').trim(),
      tags,
    })
  })

  return { items, parseErrors }
}

/**
 * 제출용 간단 파서입니다.
 * - 큰따옴표로 감싼 값 내부의 쉼표를 허용
 * - 이중 큰따옴표("") 이스케이프 허용
 */
function splitCsvLine(line: string): string[] {
  const result: string[] = []
  let current = ''
  let inQuotes = false

  for (let i = 0; i < line.length; i++) {
    const char = line[i]
    const next = line[i + 1]

    if (char === '"') {
      if (inQuotes && next === '"') {
        current += '"'
        i++
      } else {
        inQuotes = !inQuotes
      }
      continue
    }

    if (char === ',' && !inQuotes) {
      result.push(current)
      current = ''
      continue
    }

    current += char
  }

  result.push(current)

  return result.map((v) => v.trim())
}

const backdropStyle: React.CSSProperties = {
  position: 'fixed',
  inset: 0,
  backgroundColor: 'rgba(0, 0, 0, 0.35)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: 1000,
}

const modalStyle: React.CSSProperties = {
  width: 'min(920px, 92vw)',
  maxHeight: '90vh',
  overflow: 'auto',
  background: '#fff',
  borderRadius: 12,
  padding: 20,
  boxSizing: 'border-box',
}

const headerStyle: React.CSSProperties = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: 16,
}

const footerStyle: React.CSSProperties = {
  display: 'flex',
  justifyContent: 'flex-end',
  gap: 8,
  marginTop: 20,
}

const sectionStyle: React.CSSProperties = {
  marginBottom: 16,
}

const sectionTitleStyle: React.CSSProperties = {
  marginTop: 0,
  marginBottom: 8,
}

const helpTextStyle: React.CSSProperties = {
  marginTop: 0,
  marginBottom: 8,
}

const codeStyle: React.CSSProperties = {
  display: 'inline-block',
  padding: '6px 8px',
  background: '#f4f4f4',
  borderRadius: 6,
}

const textareaStyle: React.CSSProperties = {
  width: '100%',
  boxSizing: 'border-box',
  fontFamily: 'monospace',
  fontSize: 13,
  padding: 12,
}

const summaryBoxStyle: React.CSSProperties = {
  padding: 12,
  background: '#f8f8f8',
  borderRadius: 8,
  display: 'grid',
  gap: 4,
}

const resultBoxStyle: React.CSSProperties = {
  padding: 12,
  background: '#f3f8ff',
  borderRadius: 8,
  display: 'grid',
  gap: 4,
}

const errorBoxStyle: React.CSSProperties = {
  marginTop: 12,
  padding: 12,
  background: '#fff4f4',
  border: '1px solid #ffd6d6',
  borderRadius: 8,
}

const previewTableWrapperStyle: React.CSSProperties = {
  marginTop: 12,
  overflowX: 'auto',
}

const tableStyle: React.CSSProperties = {
  width: '100%',
  borderCollapse: 'collapse',
}

const thStyle: React.CSSProperties = {
  textAlign: 'left',
  borderBottom: '1px solid #ddd',
  padding: 8,
  background: '#fafafa',
}

const tdStyle: React.CSSProperties = {
  borderBottom: '1px solid #eee',
  padding: 8,
  verticalAlign: 'top',
}

const listStyle: React.CSSProperties = {
  margin: '8px 0 0 18px',
  padding: 0,
}

const closeButtonStyle: React.CSSProperties = {
  border: 'none',
  background: 'transparent',
  cursor: 'pointer',
  fontSize: 14,
}