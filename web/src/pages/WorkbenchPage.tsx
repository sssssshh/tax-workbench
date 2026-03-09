import { useState } from 'react'
import { useWorkItems } from '../hooks/useWorkItems.ts'
import { useClients } from '../hooks/useClients.ts'
import { useCreateWorkItem } from '../hooks/useWorkItems.ts'
import WorkbenchTable from '../components/workbench/WorkbenchTable.tsx'
import FilterBar from '../components/workbench/FilterBar.tsx'
import LoadingSpinner from '../components/common/LoadingSpinner.tsx'
import { workItemApi } from '../api/workItemApi.ts'
import type { WorkItemQuery } from '../types/workItem.ts'
import BulkCreateModal from '../components/workbench/BulkCreateModal.tsx'

export default function WorkbenchPage() {
  const [query, setQuery] = useState<WorkItemQuery>({
    page: 0,
    size: 20,
    sortBy: 'createdAt',
    sortDir: 'desc',
  })
  const [showCreateModal, setShowCreateModal] = useState(false)
  const { data, isLoading } = useWorkItems(query)
  const { data: clients = [] } = useClients()
  const { mutateAsync: createWorkItem } = useCreateWorkItem()
  const [showBulkModal, setShowBulkModal] = useState(false)

  const [form, setForm] = useState({
    clientId: '',
    type: 'FILING',
    assignee: '',
    dueDate: '',
    memo: '',
  })

  const handleCreate = async () => {
    if (!form.clientId) return alert('고객사를 선택해주세요.')
    await createWorkItem({
      clientId: Number(form.clientId),
      type: form.type,
      assignee: form.assignee,
      dueDate: form.dueDate,
      memo: form.memo,
      tags: [],
    })
    setShowCreateModal(false)
    setForm({ clientId: '', type: 'FILING', assignee: '', dueDate: '', memo: '' })
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <div className="bg-white border-b px-6 py-4 flex items-center justify-between">
      <h1 className="text-xl font-bold text-gray-800">🧾 Tax Workbench</h1>
      <div className="flex gap-2">
        <button
          onClick={() => setShowBulkModal(true)}
          className="px-4 py-2 bg-purple-600 text-white rounded text-sm hover:bg-purple-700 font-medium"
        >
          📥 대량 등록
        </button>
        <button
          onClick={() => setShowCreateModal(true)}
          className="px-4 py-2 bg-blue-600 text-white rounded text-sm hover:bg-blue-700 font-medium"
        >
          + 업무 추가
        </button>
      </div>
    </div>

      {/* 필터 */}
      <FilterBar
        query={query}
        onChange={setQuery}
        onExport={() => workItemApi.export(query)}
      />

      {/* 테이블 */}
      <div className="bg-white m-4 rounded-lg shadow-sm border">
        {isLoading ? (
          <LoadingSpinner />
        ) : (
          <>
            <WorkbenchTable
              items={data?.content ?? []}
              query={query}
              onQueryChange={setQuery}
            />

            {/* 페이지네이션 */}
            <div className="flex items-center justify-between px-4 py-3 border-t">
              <p className="text-sm text-gray-500">
                전체 {data?.totalElements ?? 0}건
              </p>
              <div className="flex gap-2">
                <button
                  disabled={query.page === 0}
                  onClick={() => setQuery(q => ({ ...q, page: (q.page ?? 0) - 1 }))}
                  className="px-3 py-1 border rounded text-sm disabled:opacity-40 hover:bg-gray-50"
                >
                  이전
                </button>
                <span className="px-3 py-1 text-sm">
                  {(query.page ?? 0) + 1} / {data?.totalPages ?? 1}
                </span>
                <button
                  disabled={(query.page ?? 0) + 1 >= (data?.totalPages ?? 1)}
                  onClick={() => setQuery(q => ({ ...q, page: (q.page ?? 0) + 1 }))}
                  className="px-3 py-1 border rounded text-sm disabled:opacity-40 hover:bg-gray-50"
                >
                  다음
                </button>
              </div>
            </div>
          </>
        )}
      </div>

      {/* 업무 생성 모달 */}
        {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-xl">
            <h2 className="text-lg font-bold mb-4">새 업무 추가</h2>

            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">고객사</label>
                <select
                  value={form.clientId}
                  onChange={e => setForm(f => ({ ...f, clientId: e.target.value }))}
                  className="w-full border rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">선택해주세요</option>
                  {clients.map(c => (
                    <option key={c.id} value={c.id}>{c.name} ({c.tier})</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">업무유형</label>
                <select
                  value={form.type}
                  onChange={e => setForm(f => ({ ...f, type: e.target.value }))}
                  className="w-full border rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="FILING">신고</option>
                  <option value="BOOKKEEPING">기장</option>
                  <option value="REVIEW">검토</option>
                  <option value="ETC">기타</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">담당자</label>
                <input
                  type="text"
                  value={form.assignee}
                  onChange={e => setForm(f => ({ ...f, assignee: e.target.value }))}
                  className="w-full border rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="담당자 이름"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">마감일</label>
                <input
                  type="date"
                  value={form.dueDate}
                  onChange={e => setForm(f => ({ ...f, dueDate: e.target.value }))}
                  className="w-full border rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">메모</label>
                <textarea
                  value={form.memo}
                  onChange={e => setForm(f => ({ ...f, memo: e.target.value }))}
                  className="w-full border rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  rows={3}
                  placeholder="메모 입력"
                />
              </div>
            </div>

            <div className="flex gap-2 mt-6">
              <button
                onClick={handleCreate}
                className="flex-1 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm font-medium"
              >
                추가
              </button>
              <button
                onClick={() => setShowCreateModal(false)}
                className="flex-1 py-2 border border-gray-300 rounded hover:bg-gray-50 text-sm"
              >
                취소
              </button>
            </div>
          </div>
        </div>
      )}

      {showBulkModal && (
        <BulkCreateModal
          isOpen={showBulkModal}
          onClose={() => setShowBulkModal(false)}
          onSuccess={() => {
            setShowBulkModal(false)
            setQuery(q => ({ ...q }))
          }}
        />
      )}
    </div>
    
  )
}