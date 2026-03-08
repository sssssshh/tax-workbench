import { useState, useCallback, useRef, useEffect } from 'react'
import { useVirtualizer } from '@tanstack/react-virtual'
import type { WorkItem, WorkItemStatus, ConflictError, WorkItemQuery } from '../../types/workItem.ts'
import { useUpdateWorkItem } from '../../hooks/useWorkItems.ts'
import { useKeyboardNav } from '../../hooks/useKeyboardNav.ts'
import InlineEditor from './InlineEditor.tsx'
import ConflictModal from '../common/ConflictModal.tsx'
import { useNavigate } from 'react-router-dom'
import { AxiosError } from 'axios'

interface Props {
  items: WorkItem[]
  query: WorkItemQuery
  onQueryChange: (query: WorkItemQuery) => void
}

const STATUS_COLORS: Record<WorkItemStatus, string> = {
  TODO: 'bg-gray-100 text-gray-700',
  IN_PROGRESS: 'bg-blue-100 text-blue-700',
  DONE: 'bg-green-100 text-green-700',
  HOLD: 'bg-yellow-100 text-yellow-700',
}

const STATUS_LABELS: Record<WorkItemStatus, string> = {
  TODO: 'TODO',
  IN_PROGRESS: '진행중',
  DONE: '완료',
  HOLD: '보류',
}

// 컬럼 인덱스 상수
const COL = {
  CLIENT_NAME: 0,
  BIZ_NO: 1,
  TYPE: 2,
  STATUS: 3,
  ASSIGNEE: 4,
  DUE_DATE: 5,
  MEMO: 6,
  AUDIT: 7,
}

interface ConflictState {
  fieldName: string
  myValue: string
  serverItem: WorkItem
  itemId: number
}

const ROW_HEIGHT = 48

export default function WorkbenchTable({ items, query, onQueryChange }: Props) {
  const [conflict, setConflict] = useState<ConflictState | null>(null)
  const { mutateAsync: updateWorkItem } = useUpdateWorkItem()
  const navigate = useNavigate()
  const parentRef = useRef<HTMLDivElement>(null)

  const { cursor, setCursor, isEditing, setIsEditing } = useKeyboardNav(items.length)

  // 가상화 설정
  const rowVirtualizer = useVirtualizer({
    count: items.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => ROW_HEIGHT,
    overscan: 10,
  })

  // 커서 행이 화면 밖이면 자동 스크롤
  useEffect(() => {
    rowVirtualizer.scrollToIndex(cursor.row, { align: 'auto' })
  }, [cursor.row])

  const handleSave = useCallback(async (item: WorkItem, field: string, newValue: string) => {
    setIsEditing(false)
    if (String(item[field as keyof WorkItem]) === newValue) return

    try {
      await updateWorkItem({
        id: item.id,
        data: { [field]: newValue, expectedVersion: item.version },
      })
    } catch (err) {
      const error = err as AxiosError<ConflictError>
      if (error.response?.status === 409 && error.response.data.currentData) {
        setConflict({
          fieldName: field,
          myValue: newValue,
          serverItem: error.response.data.currentData,
          itemId: item.id,
        })
      }
    }
  }, [updateWorkItem, setIsEditing])

  const handleSort = useCallback((sortBy: string) => {
    const newSortDir = query.sortBy === sortBy && query.sortDir === 'asc' ? 'desc' : 'asc'
    onQueryChange({ ...query, sortBy, sortDir: newSortDir, page: 0 })
  }, [query, onQueryChange])

  const SortIcon = ({ col }: { col: string }) => {
    if (query.sortBy !== col) return <span className="text-gray-300 ml-1">↕</span>
    return <span className="ml-1">{query.sortDir === 'asc' ? '↑' : '↓'}</span>
  }

  const handleConflictUseMyValue = useCallback(async () => {
    if (!conflict) return
    await updateWorkItem({
      id: conflict.itemId,
      data: {
        [conflict.fieldName]: conflict.myValue,
        expectedVersion: conflict.serverItem.version,
      },
    })
    setConflict(null)
  }, [conflict, updateWorkItem])

  // 셀이 현재 커서 위치인지 확인
  const isCursorCell = (rowIndex: number, colIndex: number) =>
    cursor.row === rowIndex && cursor.col === colIndex

  // 셀 클릭 시 커서 이동 + 편집 시작
  const handleCellClick = useCallback((rowIndex: number, colIndex: number) => {
    setCursor({ row: rowIndex, col: colIndex })
    setIsEditing(true)
  }, [setCursor, setIsEditing])

  // Enter/F2 키로 편집 시작
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (isEditing) return
      if (e.key === 'Enter' || e.key === 'F2') {
        e.preventDefault()
        setIsEditing(true)
      }
      if (e.key === 'Escape') {
        setIsEditing(false)
      }
    }
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [isEditing, setIsEditing])

  const renderCell = (item: WorkItem, rowIndex: number, colIndex: number) => {
    const isActive = isCursorCell(rowIndex, colIndex)
    const isEditingThis = isActive && isEditing

    const cellClass = `px-4 flex items-center h-full ${
      isActive ? 'ring-2 ring-inset ring-blue-500 bg-blue-50' : ''
    }`

    if (colIndex === COL.STATUS) {
      return (
        <div className={cellClass} style={{ width: 112 }}>
          {isEditingThis ? (
            <InlineEditor
              field="status"
              value={item.status}
              onSave={v => handleSave(item, 'status', v)}
              onCancel={() => setIsEditing(false)}
            />
          ) : (
            <span
              onClick={() => handleCellClick(rowIndex, colIndex)}
              className={`cursor-pointer px-2 py-1 rounded text-xs font-medium ${STATUS_COLORS[item.status]}`}
            >
              {STATUS_LABELS[item.status]}
            </span>
          )}
        </div>
      )
    }

    if (colIndex === COL.ASSIGNEE) {
      return (
        <div className={cellClass} style={{ width: 112 }}>
          {isEditingThis ? (
            <InlineEditor
              field="assignee"
              value={item.assignee || ''}
              onSave={v => handleSave(item, 'assignee', v)}
              onCancel={() => setIsEditing(false)}
            />
          ) : (
            <span
              onClick={() => handleCellClick(rowIndex, colIndex)}
              className="cursor-pointer hover:bg-gray-100 px-1 rounded w-full"
            >
              {item.assignee || '-'}
            </span>
          )}
        </div>
      )
    }

    if (colIndex === COL.DUE_DATE) {
      return (
        <div className={cellClass} style={{ width: 150 }}>
          {isEditingThis ? (
            <InlineEditor
              field="dueDate"
              value={item.dueDate || ''}
              onSave={v => handleSave(item, 'dueDate', v)}
              onCancel={() => setIsEditing(false)}
            />
          ) : (
            <span
              onClick={() => handleCellClick(rowIndex, colIndex)}
              className="cursor-pointer hover:bg-gray-100 px-1 rounded w-full"
            >
              {item.dueDate || '-'}
            </span>
          )}
        </div>
      )
    }

    if (colIndex === COL.MEMO) {
      return (
        <div className={`${cellClass} flex-1`}>
          {isEditingThis ? (
            <InlineEditor
              field="memo"
              value={item.memo || ''}
              onSave={v => handleSave(item, 'memo', v)}
              onCancel={() => setIsEditing(false)}
            />
          ) : (
            <span
              onClick={() => handleCellClick(rowIndex, colIndex)}
              className="cursor-pointer hover:bg-gray-100 px-1 rounded w-full truncate"
            >
              {item.memo || '-'}
            </span>
          )}
        </div>
      )
    }

    return null
  }

  return (
    <>
      {/* 키보드 단축키 안내 */}
      <div className="px-4 py-2 bg-blue-50 border-b text-xs text-blue-600 flex gap-4">
        <span>⬆⬇⬅➡ 셀 이동</span>
        <span>Enter / F2 편집 시작</span>
        <span>Esc 편집 취소</span>
        <span>Tab 다음 셀</span>
      </div>

      {/* 테이블 헤더 */}
      <table className="w-full text-sm border-collapse">
        <thead>
          <tr className="bg-gray-50 border-b">
          <th
            onClick={() => handleSort('clientName')}
            className="text-left px-4 py-3 font-semibold text-gray-600 w-36 cursor-pointer hover:bg-gray-100 select-none"
          >
            업체명<SortIcon col="clientName" />
          </th>
          <th className="text-left px-4 py-3 font-semibold text-gray-600 w-44">사업자번호</th>
          <th className="text-left px-4 py-3 font-semibold text-gray-600 w-36">업무유형</th>
          <th
            onClick={() => handleSort('status')}
            className="text-left px-4 py-3 font-semibold text-gray-600 w-28 cursor-pointer hover:bg-gray-100 select-none"
          >
            상태<SortIcon col="status" />
          </th>
          <th
            onClick={() => handleSort('assignee')}
            className="text-left px-4 py-3 font-semibold text-gray-600 w-28 cursor-pointer hover:bg-gray-100 select-none"
          >
            담당자<SortIcon col="assignee" />
          </th>
          <th
            onClick={() => handleSort('dueDate')}
            className="text-left px-4 py-3 font-semibold text-gray-600 w-36 cursor-pointer hover:bg-gray-100 select-none"
          >
            마감일<SortIcon col="dueDate" />
          </th>
          <th className="text-left px-4 py-3 font-semibold text-gray-600">메모</th>
          <th className="text-left px-4 py-3 font-semibold text-gray-600 w-24">이력</th>
        </tr>
        </thead>
      </table>

      {/* 가상화 스크롤 영역 */}
      <div
        ref={parentRef}
        className="overflow-auto outline-none"
        style={{ height: '600px' }}
        tabIndex={0} // 포커스 받을 수 있게
      >
        <div style={{ height: `${rowVirtualizer.getTotalSize()}px`, position: 'relative' }}>
          {rowVirtualizer.getVirtualItems().map(virtualRow => {
            const item = items[virtualRow.index]
            const rowIndex = virtualRow.index
            return (
              <div
                key={item.id}
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  width: '100%',
                  height: `${ROW_HEIGHT}px`,
                  transform: `translateY(${virtualRow.start}px)`,
                }}
                className={`flex items-center border-b transition-colors ${
                  cursor.row === rowIndex ? 'bg-blue-50' : 'hover:bg-gray-50'
                }`}
                onClick={() => setCursor(prev => ({ ...prev, row: rowIndex }))}
              >
                <div className="px-4 font-medium w-36 truncate">{item.clientName}</div>
                <div className="px-4 text-gray-500 w-44 truncate">{item.bizNo}</div>
                <div className="px-4 w-36 truncate">{item.type}</div>
                {renderCell(item, rowIndex, COL.STATUS)}
                {renderCell(item, rowIndex, COL.ASSIGNEE)}
                {renderCell(item, rowIndex, COL.DUE_DATE)}
                {renderCell(item, rowIndex, COL.MEMO)}
                <div className="px-4 w-24">
                  <button
                    onClick={() => navigate(`/audit/${item.id}`)}
                    className="text-blue-600 hover:underline text-xs"
                  >
                    이력보기
                  </button>
                </div>
              </div>
            )
          })}
        </div>
      </div>

      {conflict && (
        <ConflictModal
          fieldName={conflict.fieldName}
          myValue={conflict.myValue}
          serverItem={conflict.serverItem}
          onUseMyValue={handleConflictUseMyValue}
          onUseServerValue={() => setConflict(null)}
          onClose={() => setConflict(null)}
        />
      )}
    </>
  )
}