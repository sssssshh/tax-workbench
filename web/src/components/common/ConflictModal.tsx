import type { WorkItem } from '../../types/workItem.ts'

interface Props {
  fieldName: string
  myValue: string
  serverItem: WorkItem
  onUseMyValue: () => void
  onUseServerValue: () => void
  onClose: () => void
}

export default function ConflictModal({
  fieldName,
  myValue,
  serverItem,
  onUseMyValue,
  onUseServerValue,
  onClose,
}: Props) {
  const serverValue = serverItem[fieldName as keyof WorkItem] as string

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-xl">
        <h2 className="text-lg font-bold text-red-600 mb-2">⚠️ 수정 충돌 발생</h2>
        <p className="text-sm text-gray-600 mb-4">
          다른 사용자가 이미 이 항목을 수정했습니다. 어떤 값을 사용할지 선택해주세요.
        </p>

        <div className="space-y-3 mb-6">
          <div className="p-3 bg-blue-50 rounded border border-blue-200">
            <p className="text-xs text-blue-600 font-semibold mb-1">내가 입력한 값</p>
            <p className="text-sm font-medium">{myValue}</p>
          </div>
          <div className="p-3 bg-gray-50 rounded border border-gray-200">
            <p className="text-xs text-gray-600 font-semibold mb-1">서버의 최신 값</p>
            <p className="text-sm font-medium">{serverValue}</p>
          </div>
        </div>

        <div className="flex gap-2">
          <button
            onClick={onUseMyValue}
            className="flex-1 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm font-medium"
          >
            내 값 사용
          </button>
          <button
            onClick={onUseServerValue}
            className="flex-1 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 text-sm font-medium"
          >
            서버 값 사용
          </button>
          <button
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 rounded hover:bg-gray-50 text-sm"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  )
}