import axiosInstance from './axios.ts'
import type { Client } from '../types/client.ts'

export const clientApi = {
  findAll: async (): Promise<Client[]> => {
    const res = await axiosInstance.get('/api/v1/clients')
    return res.data.data
  },

  create: async (data: {
    name: string
    bizNo: string
    type: string
    tier: string
  }): Promise<Client> => {
    const res = await axiosInstance.post('/api/v1/clients', data)
    return res.data.data
  },
}