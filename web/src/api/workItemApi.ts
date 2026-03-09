import axiosInstance from './axios.ts'
import type { WorkItem, WorkItemPage, WorkItemQuery, AuditLog } from '../types/workItem.ts'

export type BulkCreateApiResponse = {
  savedCount: number
  skippedCount: number
  errors: string[]
}

export const workItemApi = {
  findAll: async (query: WorkItemQuery): Promise<WorkItemPage> => {
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
  }[]): Promise<BulkCreateApiResponse> => {
    const res = await axiosInstance.post('/api/v1/work-items/bulk', { items })
    return res.data.data
  },

  export: (query: WorkItemQuery): void => {
    const params = new URLSearchParams()

    Object.entries(query).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params.append(key, String(value))
      }
    })

    window.open(`http://localhost:8080/api/v1/work-items/export?${params.toString()}`)
  },

  getAuditLogs: async (id: number): Promise<AuditLog[]> => {
    const res = await axiosInstance.get(`/api/v1/work-items/${id}/audit`)
    return res.data.data
  },
}