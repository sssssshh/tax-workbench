import axiosInstance from './axios.ts'
import type { WorkItem, WorkItemPage, WorkItemQuery, AuditLog } from '../types/workItem.ts'

export const workItemApi = {
  findAll: async (query: WorkItemQuery): Promise<WorkItemPage> => {
    // 빈 값 제거 후 전송
    const params = Object.fromEntries(
      Object.entries(query).filter(([, v]) => v !== undefined && v !== '' && v !== null)
    )
    const res = await axiosInstance.get('/api/v1/work-items', { params })
    return res.data.data
  },

  create: async (data: {
    clientId: number
    type: string
    assignee: string
    dueDate: string
    memo: string
    tags: string[]
  }): Promise<WorkItem> => {
    const res = await axiosInstance.post('/api/v1/work-items', data)
    return res.data.data
  },

  update: async (
    id: number,
    data: {
      status?: string
      assignee?: string
      dueDate?: string
      memo?: string
      tags?: string[]
      expectedVersion: number
    }
  ): Promise<WorkItem> => {
    const res = await axiosInstance.patch(`/api/v1/work-items/${id}`, data)
    return res.data.data
  },

  bulkCreate: async (items: {
    clientId: number
    type: string
    assignee: string
    dueDate: string
    memo: string
    tags: string[]
  }[]): Promise<{ savedCount: number }> => {
    const res = await axiosInstance.post('/api/v1/work-items/bulk', { items })
    return res.data.data
  },

  export: (query: WorkItemQuery): void => {
    const params = new URLSearchParams()
    if (query.clientName) params.append('clientName', query.clientName)
    if (query.status) params.append('status', query.status)
    if (query.assignee) params.append('assignee', query.assignee)
    window.open(`http://localhost:8080/api/v1/work-items/export?${params.toString()}`)
  },

  getAuditLogs: async (id: number): Promise<AuditLog[]> => {
    const res = await axiosInstance.get(`/api/v1/work-items/${id}/audit`)
    return res.data.data
  },
}