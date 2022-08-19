package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class OrderService {

    private ConcurrentHashMap<Long, AtomicReference<Order>> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), new AtomicReference<>(order));
        return order.getId();
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        Order currOrder, updated;
        do {
            currOrder = currentOrders.get(orderId).get();
            updated = currOrder.withPaymentInfo(paymentInfo);
        } while (!currentOrders.get(orderId).compareAndSet(currOrder, updated));

        if (updated.checkStatus()) {
            deliver(updated);
        }
    }

    public void setPacked(long orderId) {
        Order curr, updated;
        do {
            curr = currentOrders.get(orderId).get();
            updated = curr.doPack();
        } while (!currentOrders.get(orderId).compareAndSet(curr, updated));

        if (updated.checkStatus()) {
            deliver(updated);
        }
    }

    private void deliver(Order order) {
        Order curr, updated;
        do {
            curr = currentOrders.get(order.getId()).get();
            updated = order.withStatus(Order.Status.DELIVERED);
        } while (!currentOrders.get(order.getId()).compareAndSet(curr, updated));
    }

    public synchronized boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).get().getStatus().equals(Order.Status.DELIVERED);
    }
}
