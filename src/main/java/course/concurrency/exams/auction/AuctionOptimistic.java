package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(-1L, -1L, -1L));

    public boolean propose(Bid bid) {
        Bid currentBid;
        do {
            currentBid = latestBid.get();
            if (bid.price <= currentBid.price)
                return false;

        } while (!latestBid.compareAndSet(currentBid, bid));

        notifier.sendOutdatedMessage(currentBid);

        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
