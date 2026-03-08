import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { workItemApi } from '../api/workItemApi.ts'
import type { WorkItemQuery } from '../types/workItem.ts'

export const useWorkItems = (query: WorkItemQuery) => {
  return useQuery({
    queryKey: ['workItems', query],
    queryFn: () => workItemApi.findAll(query),
  })
}

export const useCreateWorkItem = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: workItemApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workItems'] })
    },
  })
}

export const useUpdateWorkItem = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: {
      id: number
      data: {
        status?: string
        assignee?: string
        dueDate?: string
        memo?: string
        tags?: string[]
        expectedVersion: number
      }
    }) => workItemApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workItems'] })
    },
    onError: () => {
      queryClient.invalidateQueries({ queryKey: ['workItems'] })
    }
  })
}

export const useAuditLogs = (id: number) => {
  return useQuery({
    queryKey: ['auditLogs', id],
    queryFn: () => workItemApi.getAuditLogs(id),
    enabled: !!id,
  })
}