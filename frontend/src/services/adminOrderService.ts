import { apiClient } from './api/http'
import type { Order, OrderStatus } from '@/types/api'

export async function getAllOrders() {
  const { data } = await apiClient.get<Order[]>('/admin/orders')
  return data
}

export async function updateOrderStatus(orderId: number, status: OrderStatus) {
  const { data } = await apiClient.put<Order>(`/admin/orders/${orderId}/status?status=${status}`)
  return data
}

