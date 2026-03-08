import type { WorkItemQuery, WorkItemStatus } from '../../types/workItem.ts'

interface Props {
  query: WorkItemQuery
  onChange: (query: WorkItemQuery) => void
  onExport: () => void
}

const STATUS_OPTIONS: { label: string; value: WorkItemStatus | '' }[] = [
  { label: '전체', value: '' },
  { label: 'TODO', value: 'TODO' },
  { label: '진행중', value: 'IN_PROGRESS' },
  { label: '완료', value: 'DONE' },
  { label: '보류', value: 'HOLD' },
]

export default function FilterBar({ query, onChange, onExport }: Props) {
  return (
    <div className="flex items-center gap-3 p-4 bg-white border-b">
      <input
        type="text"
        placeholder="업체명 검색"
        value={query.clientName || ''}
        onChange={e => onChange({ ...query, clientName: e.target.value, page: 0 })}
        className="border rounded px-3 py-1.5 text-sm w-40 focus:outline-none focus:ring-2 focus:ring-blue-500"
      />

      <select
        value={query.status || ''}
        onChange={e => onChange({
          ...query,
          status: e.target.value as WorkItemStatus || undefined,
          page: 0
        })}
        className="border rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        {STATUS_OPTIONS.map(opt => (
          <option key={opt.value} value={opt.value}>{opt.label}</option>
        ))}
      </select>

      <input
        type="text"
        placeholder="담당자 검색"
        value={query.assignee || ''}
        onChange={e => onChange({ ...query, assignee: e.target.value, page: 0 })}
        className="border rounded px-3 py-1.5 text-sm w-32 focus:outline-none focus:ring-2 focus:ring-blue-500"
      />

      <button
        onClick={onExport}
        className="ml-auto px-4 py-1.5 bg-green-600 text-white rounded text-sm hover:bg-green-700 font-medium"
      >
        CSV 내보내기
      </button>
    </div>
  )
}