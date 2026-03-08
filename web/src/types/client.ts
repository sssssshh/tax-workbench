export type ClientType = 'INDIVIDUAL' | 'CORPORATE'
export type ClientStatus = 'ACTIVE' | 'INACTIVE'
export type ClientTier = 'BASIC' | 'PREMIUM' | 'VIP'

export interface Client {
  id: number
  name: string
  bizNo: string
  type: ClientType
  status: ClientStatus
  tier: ClientTier
  createdAt: string
  updatedAt: string
}