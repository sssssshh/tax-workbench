import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { clientApi } from '../api/clientApi.ts'

export const useClients = () => {
  return useQuery({
    queryKey: ['clients'],
    queryFn: clientApi.findAll,
  })
}

export const useCreateClient = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: clientApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clients'] })
    },
  })
}