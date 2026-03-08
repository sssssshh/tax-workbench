import { useParams, useNavigate } from 'react-router-dom'
import { useAuditLogs } from '../hooks/useWorkItems.ts'
import LoadingSpinner from '../components/common/LoadingSpinner.tsx'

export default function AuditPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { data: logs = [], isLoading } = useAuditLogs(Number(id))

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b px-6 py-4 flex items-center gap-4">
        <button
          onClick={() => navigate('/')}
          className="text-blue-600 hover:underline text-sm"
        >
          ← 목록으로
        </button>
        <h1 className="text-xl font-bold text-gray-800">변경 이력</h1>
      </div>

      <div className="bg-white m-4 rounded-lg shadow-sm border">
        {isLoading ? (
          <LoadingSpinner />
        ) : logs.length === 0 ? (
          <div className="p-8 text-center text-gray-400">변경 이력이 없습니다.</div>
        ) : (
          <table className="w-full text-sm border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">필드</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">이전 값</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">변경 값</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">변경자</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">변경일시</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log, i) => (
                <tr key={i} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium">{log.fieldName}</td>
                  <td className="px-4 py-3 text-red-500">{log.oldValue || '-'}</td>
                  <td className="px-4 py-3 text-green-600">{log.newValue || '-'}</td>
                  <td className="px-4 py-3">{log.changedBy}</td>
                  <td className="px-4 py-3 text-gray-500">
                    {new Date(log.changedAt).toLocaleString('ko-KR')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}