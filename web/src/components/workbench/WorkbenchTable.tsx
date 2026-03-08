import { useState, useCallback } from 'react'
import type { WorkItem, WorkItemStatus } from '../../types/workItem.ts'
import { useUpdateWorkItem } from '../../hooks/useWorkItems.ts'
import InlineEditor from './InlineEditor.tsx'
import ConflictModal from '../common/ConflictModal.tsx'
import { useNavigate } from 'react-router-dom'
import { AxiosError } from 'axios'

interface Props {
  items: WorkItem[]
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

interface EditingCell {
  rowId: number
  field: string
  value: string
}

interface ConflictState {
  fieldName: string
  myValue: string
  serverItem: WorkItem
  itemId: number
}

export default function WorkbenchTable({ items }: Props) {
  const [editingCell, setEditingCell] = useState<EditingCell | null>(null)
  const [conflict, setConflict] = useState<ConflictState | null>(null)
  const { mutateAsync: updateWorkItem } = useUpdateWorkItem()
  const navigate = useNavigate()

  const handleCellClick = useCallback((item: WorkItem, field: string, value: string) => {
    setEditingCell({ rowId: item.id, field, value })
  }, [])

  const handleSave = useCallback(async (item: WorkItem, field: string, newValue: string) => {
    setEditingCell(null)
    if (String(item[field as keyof WorkItem]) === newValue) return

    try {
      await updateWorkItem({
        id: item.id,
        data: {
          [field]: newValue,
          expectedVersion: item.version,
        },
      })
    } catch (err) {
      const error = err as AxiosError<{
        currentVersion: number
        currentData: WorkItem
      }>
      if (error.response?.status === 409) {
        setConflict({
          fieldName: field,
          myValue: newValue,
          serverItem: {
            ...item,
            version: error.response.data.currentVersion,
          },
          itemId: item.id,
        })
      }
    }
  }, [updateWorkItem])

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

  return (
    <>
      <div className="overflow-x-auto">
        <table className="w-full text-sm border-collapse">
          <thead>
            <tr className="bg-gray-50 border-b">
              <th className="text-left px-4 py-3 font-semibold text-gray-600">업체명</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">사업자번호</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">업무유형</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">상태</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">담당자</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">마감일</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">메모</th>
              <th className="text-left px-4 py-3 font-semibold text-gray-600">이력</th>
            </tr>
          </thead>
          <tbody>
            {items.map(item => (
              <tr key={item.id} className="border-b hover:bg-gray-50 transition-colors">
                <td className="px-4 py-3 font-medium">{item.clientName}</td>
                <td className="px-4 py-3 text-gray-500">{item.bizNo}</td>
                <td className="px-4 py-3">{item.type}</td>

                {/* 상태 - 인라인 편집 */}
                <td className="px-4 py-3">
                  {editingCell?.rowId === item.id && editingCell.field === 'status' ? (
                    <InlineEditor
                      field="status"
                      value={editingCell.value}
                      onSave={v => handleSave(item, 'status', v)}
                      onCancel={() => setEditingCell(null)}
                    />
                  ) : (
                    <span
                      onClick={() => handleCellClick(item, 'status', item.status)}
                      className={`cursor-pointer px-2 py-1 rounded text-xs font-medium ${STATUS_COLORS[item.status]}`}
                    >
                      {STATUS_LABELS[item.status]}
                    </span>
                  )}
                </td>

                {/* 담당자 - 인라인 편집 */}
                <td className="px-4 py-3">
                  {editingCell?.rowId === item.id && editingCell.field === 'assignee' ? (
                    <InlineEditor
                      field="assignee"
                      value={editingCell.value}
                      onSave={v => handleSave(item, 'assignee', v)}
                      onCancel={() => setEditingCell(null)}
                    />
                  ) : (
                    <span
                      onClick={() => handleCellClick(item, 'assignee', item.assignee || '')}
                      className="cursor-pointer hover:bg-gray-100 px-1 rounded"
                    >
                      {item.assignee || '-'}
                    </span>
                  )}
                </td>

                {/* 마감일 - 인라인 편집 */}
                <td className="px-4 py-3">
                  {editingCell?.rowId === item.id && editingCell.field === 'dueDate' ? (
                    <InlineEditor
                      field="dueDate"
                      value={editingCell.value}
                      onSave={v => handleSave(item, 'dueDate', v)}
                      onCancel={() => setEditingCell(null)}
                    />
                  ) : (
                    <span
                      onClick={() => handleCellClick(item, 'dueDate', item.dueDate || '')}
                      className="cursor-pointer hover:bg-gray-100 px-1 rounded"
                    >
                      {item.dueDate || '-'}
                    </span>
                  )}
                </td>

                {/* 메모 - 인라인 편집 */}
                <td className="px-4 py-3 max-w-xs truncate">
                  {editingCell?.rowId === item.id && editingCell.field === 'memo' ? (
                    <InlineEditor
                      field="memo"
                      value={editingCell.value}
                      onSave={v => handleSave(item, 'memo', v)}
                      onCancel={() => setEditingCell(null)}
                    />
                  ) : (
                    <span
                      onClick={() => handleCellClick(item, 'memo', item.memo || '')}
                      className="cursor-pointer hover:bg-gray-100 px-1 rounded"
                    >
                      {item.memo || '-'}
                    </span>
                  )}
                </td>

                <td className="px-4 py-3">
                  <button
                    onClick={() => navigate(`/audit/${item.id}`)}
                    className="text-blue-600 hover:underline text-xs"
                  >
                    이력보기
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* 충돌 모달 */}
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