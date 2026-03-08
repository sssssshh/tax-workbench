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
  const handleReset = () => {
    onChange({
      page: 0,
      size: 20,
      sortBy: 'createdAt',
      sortDir: 'desc',
    })
  }

  return (
    <div className="flex flex-wrap items-center gap-3 p-4 bg-white border-b">
      {/* 업체명 */}
      <input
        type="text"
        placeholder="업체명 검색"
        value={query.clientName || ''}
        onChange={e => onChange({ ...query, clientName: e.target.value, page: 0 })}
        className="border rounded px-3 py-1.5 text-sm w-36 focus:outline-none focus:ring-2 focus:ring-blue-500"
      />

      {/* 상태 */}
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

      {/* 담당자 */}
      <input
        type="text"
        placeholder="담당자 검색"
        value={query.assignee || ''}
        onChange={e => onChange({ ...query, assignee: e.target.value, page: 0 })}
        className="border rounded px-3 py-1.5 text-sm w-28 focus:outline-none focus:ring-2 focus:ring-blue-500"
      />

      {/* 마감일 범위 */}
      <div className="flex items-center gap-1">
        <span className="text-sm text-gray-500">마감일</span>
        <input
          type="date"
          value={query.dueDateFrom || ''}
          onChange={e => onChange({ ...query, dueDateFrom: e.target.value, page: 0 })}
          className="border rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <span className="text-gray-400">~</span>
        <input
          type="date"
          value={query.dueDateTo || ''}
          onChange={e => onChange({ ...query, dueDateTo: e.target.value, page: 0 })}
          className="border rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* 초기화 */}
      <button
        onClick={handleReset}
        className="px-3 py-1.5 border border-gray-300 rounded text-sm hover:bg-gray-50 text-gray-600"
      >
        초기화
      </button>

      {/* CSV 내보내기 */}
      <button
        onClick={onExport}
        className="ml-auto px-4 py-1.5 bg-green-600 text-white rounded text-sm hover:bg-green-700 font-medium"
      >
        CSV 내보내기
      </button>
    </div>
  )
}