import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { workItemApi } from '../../api/workItemApi.ts'
import type { Client } from '../../types/client.ts'

interface Props {
  clients: Client[]
  onClose: () => void
}

const TEMPLATE = `clientId,type,assignee,dueDate,memo
1,FILING,김세무,2026-03-31,법인세 신고
1,BOOKKEEPING,이세무,2026-03-31,기장 업무
1,REVIEW,박세무,2026-03-31,검토 업무`

export default function BulkCreateModal({ clients, onClose }: Props) {
  const [csvText, setCsvText] = useState(TEMPLATE)
  const [result, setResult] = useState<{ savedCount: number } | null>(null)
  const [error, setError] = useState<string | null>(null)
  const queryClient = useQueryClient()

  const { mutate: bulkCreate, isPending } = useMutation({
    mutationFn: (items: {
      clientId: number
      type: string
      assignee: string
      dueDate: string
      memo: string
      tags: string[]
    }[]) => workItemApi.bulkCreate(items),
    onSuccess: (data) => {
      setResult(data)
      queryClient.invalidateQueries({ queryKey: ['workItems'] })
    },
    onError: () => {
      setError('대량 등록 중 오류가 발생했습니다.')
    }
  })

  const handleSubmit = () => {
    setError(null)
    setResult(null)

    try {
      const lines = csvText.trim().split('\n')
      const headers = lines[0].split(',').map(h => h.trim())
      
      const items = lines.slice(1).map(line => {
        const values = line.split(',').map(v => v.trim())
        const row: Record<string, string> = {}
        headers.forEach((h, i) => row[h] = values[i] || '')

        return {
          clientId: Number(row.clientId),
          type: row.type,
          assignee: row.assignee,
          dueDate: row.dueDate,
          memo: row.memo,
          tags: [],
        }
      }).filter(item => item.clientId && item.type)

      if (items.length === 0) {
        setError('유효한 데이터가 없습니다.')
        return
      }

      bulkCreate(items)
    } catch {
      setError('CSV 형식이 올바르지 않습니다.')
    }
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-2xl shadow-xl">
        <h2 className="text-lg font-bold mb-1">📥 대량 업무 등록</h2>
        <p className="text-sm text-gray-500 mb-4">
          CSV 형식으로 여러 업무를 한 번에 등록할 수 있습니다.
        </p>

        {/* 고객사 ID 안내 */}
        <div className="mb-3 p-3 bg-gray-50 rounded border text-xs">
          <p className="font-semibold text-gray-600 mb-1">📋 등록된 고객사 ID</p>
          <div className="flex flex-wrap gap-2">
            {clients.map(c => (
              <span key={c.id} className="px-2 py-1 bg-white border rounded">
                {c.id}: {c.name} ({c.tier})
              </span>
            ))}
          </div>
        </div>

        {/* 업무 유형 안내 */}
        <div className="mb-3 p-3 bg-blue-50 rounded border text-xs text-blue-700">
          <p className="font-semibold mb-1">📌 업무 유형 (type)</p>
          <span>FILING(신고) / BOOKKEEPING(기장) / REVIEW(검토) / ETC(기타)</span>
        </div>

        {/* CSV 입력 */}
        <textarea
          value={csvText}
          onChange={e => setCsvText(e.target.value)}
          rows={10}
          className="w-full border rounded px-3 py-2 text-sm font-mono focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="CSV 형식으로 입력해주세요"
        />

        {/* 오류 메시지 */}
        {error && (
          <div className="mt-2 p-3 bg-red-50 border border-red-200 rounded text-sm text-red-600">
            ❌ {error}
          </div>
        )}

        {/* 성공 메시지 */}
        {result && (
          <div className="mt-2 p-3 bg-green-50 border border-green-200 rounded text-sm text-green-600">
            ✅ {result.savedCount}건이 성공적으로 등록되었습니다!
          </div>
        )}

        <div className="flex gap-2 mt-4">
          <button
            onClick={handleSubmit}
            disabled={isPending}
            className="flex-1 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm font-medium disabled:opacity-50"
          >
            {isPending ? '등록 중...' : '대량 등록'}
          </button>
          <button
            onClick={onClose}
            className="flex-1 py-2 border border-gray-300 rounded hover:bg-gray-50 text-sm"
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  )
}