import { useState, useCallback, useEffect } from 'react'

interface CursorPosition {
  row: number
  col: number
}

const EDITABLE_COLS = [3, 4, 5, 6] // 상태, 담당자, 마감일, 메모 컬럼 인덱스

export const useKeyboardNav = (rowCount: number) => {
  const [cursor, setCursor] = useState<CursorPosition>({ row: 0, col: 3 })
  const [isEditing, setIsEditing] = useState(false)

  const moveCursor = useCallback((direction: 'up' | 'down' | 'left' | 'right') => {
    if (isEditing) return
    setCursor(prev => {
      switch (direction) {
        case 'up':
          return { ...prev, row: Math.max(0, prev.row - 1) }
        case 'down':
          return { ...prev, row: Math.min(rowCount - 1, prev.row + 1) }
        case 'left': {
          const prevEditableIdx = EDITABLE_COLS.indexOf(prev.col)
          const nextIdx = Math.max(0, prevEditableIdx - 1)
          return { ...prev, col: EDITABLE_COLS[nextIdx] }
        }
        case 'right': {
          const nextEditableIdx = EDITABLE_COLS.indexOf(prev.col)
          const nextIdx = Math.min(EDITABLE_COLS.length - 1, nextEditableIdx + 1)
          return { ...prev, col: EDITABLE_COLS[nextIdx] }
        }
        default:
          return prev
      }
    })
  }, [isEditing, rowCount])

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // 편집 중엔 네비게이션 막음
      if (isEditing) return

      switch (e.key) {
        case 'ArrowUp':
          e.preventDefault()
          moveCursor('up')
          break
        case 'ArrowDown':
          e.preventDefault()
          moveCursor('down')
          break
        case 'ArrowLeft':
          e.preventDefault()
          moveCursor('left')
          break
        case 'ArrowRight':
          e.preventDefault()
          moveCursor('right')
          break
        case 'Tab':
          e.preventDefault()
          moveCursor(e.shiftKey ? 'left' : 'right')
          break
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [isEditing, moveCursor])

  return { cursor, setCursor, isEditing, setIsEditing }
}