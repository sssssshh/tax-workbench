import { AxiosError } from 'axios'
import type { AxiosResponse } from 'axios'

export type ApiError = {
  code: string
  details?: Record<string, unknown>
}

export type ApiResponse<T> = {
  success: boolean
  data: T
  message: string | null
}

export const unwrapApiData = <T>(response: AxiosResponse<ApiResponse<T>>): T => response.data.data

type ErrorPayload = {
  message?: string | null
  data?: {
    code?: string
    details?: {
      fieldErrors?: Record<string, string>
      currentData?: unknown
      [key: string]: unknown
    }
  }
  currentData?: unknown
  [key: string]: unknown
}

export const getApiErrorMessage = (
  error: unknown,
  fallbackMessage: string
): string => {
  const payload = (error as AxiosError<ErrorPayload>)?.response?.data
  return payload?.message || fallbackMessage
}

export const getApiFieldErrors = (error: unknown): Record<string, string> => {
  const payload = (error as AxiosError<ErrorPayload>)?.response?.data
  const fieldErrors = payload?.data?.details?.fieldErrors
  if (!fieldErrors || typeof fieldErrors !== 'object') return {}
  return fieldErrors
}

export const getApiErrorMessages = (
  error: unknown,
  fallbackMessage: string
): string[] => {
  const fieldErrors = getApiFieldErrors(error)
  const messages = Object.entries(fieldErrors).map(
    ([field, message]) => `${field}: ${String(message)}`
  )
  return messages.length > 0 ? messages : [getApiErrorMessage(error, fallbackMessage)]
}

export const getConflictCurrentData = <T>(error: unknown): T | undefined => {
  const payload = (error as AxiosError<ErrorPayload>)?.response?.data
  return (payload?.currentData ?? payload?.data?.details?.currentData) as T | undefined
}
