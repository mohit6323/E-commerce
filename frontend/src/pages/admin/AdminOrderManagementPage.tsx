import { useEffect, useState } from 'react'
import { Loader } from '@/components/feedback/Loader'
import { Alert } from '@/components/feedback/Alert'
import { EmptyState } from '@/components/feedback/EmptyState'
import { Button } from '@/components/forms/Button'
import { SelectField } from '@/components/forms/SelectField'
import { formatCurrency } from '@/utils/currency'
import { toAppError } from '@/utils/errors'
import { getAllOrders, updateOrderStatus } from '@/services/adminOrderService'
import type { Order, OrderStatus } from '@/types/api'

const ORDER_STATUS_OPTIONS: { value: OrderStatus; label: string }[] = [
  { value: 'PENDING', label: 'Pending' },
  { value: 'PROCESSING', label: 'Processing' },
  { value: 'SHIPPED', label: 'Shipped' },
  { value: 'DELIVERED', label: 'Delivered' },
  { value: 'CANCELLED', label: 'Cancelled' },
]

export function AdminOrderManagementPage() {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [busyOrderId, setBusyOrderId] = useState<number | null>(null)
  const [notice, setNotice] = useState('')

  useEffect(() => {
    async function loadOrders() {
      setLoading(true)
      setError('')
      try {
        const data = await getAllOrders()
        setOrders(data)
      } catch (err) {
        setError(toAppError(err).message)
      } finally {
        setLoading(false)
      }
    }
    loadOrders()
  }, [])

  const handleStatusChange = async (orderId: number, status: OrderStatus) => {
    setBusyOrderId(orderId)
    setError('')
    setNotice('')
    try {
      const updated = await updateOrderStatus(orderId, status)
      setOrders((current) =>
        current.map((order) => (order.id === orderId ? updated : order))
      )
      setNotice(`Order #${updated.orderNumber} status updated to ${updated.status}.`)
    } catch (err) {
      setError(toAppError(err).message)
    } finally {
      setBusyOrderId(null)
    }
  }

  if (loading) return <Loader label="Loading all orders…" />
  if (error) return <Alert variant="error">{error}</Alert>
  if (!orders.length) return <EmptyState title="No orders found" description="No orders have been placed yet." />

  return (
    <div className="stack-lg">
      <section className="hero-panel hero-panel--compact">
        <span className="eyebrow">Admin Panel</span>
        <h1>Manage customer orders</h1>
        <p className="hero-panel__copy">Track, update, and fulfill orders to keep your customers happy.</p>
      </section>
      {notice ? <Alert variant="success">{notice}</Alert> : null}
      <div className="content-grid">
        {orders.map((order) => (
          <section className="surface stack-md" key={order.id}>
            <h2>Order #{order.orderNumber}</h2>
            <p>Status: <strong>{order.status}</strong></p>
            <SelectField
              label="Update Status"
              name={`status-${order.id}`}
              value={order.status}
              onChange={(e) => handleStatusChange(order.id, e.target.value as OrderStatus)}
              options={ORDER_STATUS_OPTIONS}
              disabled={busyOrderId === order.id}
            />
            <p>Total: <strong>{formatCurrency(order.totalAmount)}</strong></p>
            <div className="stack-sm">
              {order.orderItems.map((item) => (
                <div className="summary-row" key={item.id}>
                  <span>Product #{item.productId} x {item.quantity}</span>
                  <strong>{formatCurrency(item.price)}</strong>
                </div>
              ))}
            </div>
            <Button
              busy={busyOrderId === order.id}
              type="button"
              onClick={() => handleStatusChange(order.id, order.status)}
              disabled
              style={{ display: 'none' }}
            >
              Update
            </Button>
          </section>
        ))}
      </div>
    </div>
  )
}
// ...existing code...
