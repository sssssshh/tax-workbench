import axiosInstance from './axios.ts'
import { unwrapApiData } from './contracts.ts'
import type { Client } from '../types/client.ts'

type ClientListPayload = {
  items: Client[]
  count: number
}

export const clientApi = {
  findAll: async (): Promise<Client[]> => {
    const payload = unwrapApiData<ClientListPayload | Client[]>(
      await axiosInstance.get('/api/v1/clients')
    )
    return Array.isArray(payload) ? payload : (payload?.items ?? [])
  },

  create: async (data: {
    name: string
    bizNo: string
    type: string
    tier: string
  }): Promise<Client> => {
    return unwrapApiData<Client>(await axiosInstance.post('/api/v1/clients', data))
  },
}
