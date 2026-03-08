import { useState, useEffect, useRef } from 'react'
import type { WorkItemStatus } from '../../types/workItem.ts'

interface Props {
  field: string
  value: string
  onSave: (value: string) => void
  onCancel: () => void
}

const STATUS_OPTIONS: WorkItemStatus[] = ['TODO', 'IN_PROGRESS', 'DONE', 'HOLD']

const STATUS_LABELS: Record<WorkItemStatus, string> = {
  TODO: 'TODO',
  IN_PROGRESS: '진행중',
  DONE: '완료',
  HOLD: '보류',
}

export default function InlineEditor({ field, value, onSave, onCancel }: Props) {
  const [editValue, setEditValue] = useState(value)
  const ref = useRef<HTMLInputElement & HTMLSelectElement>(null)

  useEffect(() => {
    ref.current?.focus()
  }, [])

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') onSave(editValue)
    if (e.key === 'Escape') onCancel()
  }

  if (field === 'status') {
    return (
      <select
        ref={ref as React.RefObject<HTMLSelectElement>}
        value={editValue}
        onChange={e => setEditValue(e.target.value)}
        onBlur={() => onSave(editValue)}
        onKeyDown={handleKeyDown}
        className="w-full border rounded px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        {STATUS_OPTIONS.map(s => (
          <option key={s} value={s}>{STATUS_LABELS[s]}</option>
        ))}
      </select>
    )
  }

  if (field === 'dueDate') {
    return (
      <input
        ref={ref as React.RefObject<HTMLInputElement>}
        type="date"
        value={editValue}
        onChange={e => setEditValue(e.target.value)}
        onBlur={() => onSave(editValue)}
        onKeyDown={handleKeyDown}
        className="w-full border rounded px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
    )
  }

  return (
    <input
      ref={ref as React.RefObject<HTMLInputElement>}
      type="text"
      value={editValue}
      onChange={e => setEditValue(e.target.value)}
      onBlur={() => onSave(editValue)}
      onKeyDown={handleKeyDown}
      className="w-full border rounded px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
    />
  )
}