export type WorkItemType = 'FILING' | 'BOOKKEEPING' | 'REVIEW' | 'ETC'
export type WorkItemStatus = 'TODO' | 'IN_PROGRESS' | 'DONE' | 'HOLD'

export interface WorkItem {
  id: number
  clientId: number
  clientName: string
  bizNo: string
  type: WorkItemType
  status: WorkItemStatus
  assignee: string
  dueDate: string
  tags: string[]
  memo: string
  createdAt: string
  updatedAt: string
  version: number
}

export interface WorkItemPage {
  content: WorkItem[]
  totalElements: number
  totalPages: number
  currentPage: number
  size: number
}

export interface WorkItemQuery {
  clientName?: string
  status?: WorkItemStatus
  assignee?: string
  dueDateFrom?: string
  dueDateTo?: string
  page?: number
  size?: number
  sortBy?: string
  sortDir?: 'asc' | 'desc'
}

export interface AuditLog {
  id: number
  entityType: string
  entityId: number
  fieldName: string
  oldValue: string
  newValue: string
  changedBy: string
  changedAt: string
}

export interface ConflictData {
  currentVersion: number
  expectedVersion: number
  message: string
}

export interface ConflictError {
  error: string
  message: string
  currentVersion: number
  expectedVersion: number
  currentData: WorkItem
}