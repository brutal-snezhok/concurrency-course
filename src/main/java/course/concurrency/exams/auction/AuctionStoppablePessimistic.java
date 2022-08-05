package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(-1L, -1L, -1L);
    private volatile boolean isOpen = true;
    private final Object lock = new Object();

    public boolean propose(Bid bid) {
        // this condition filter bids with lower price and seriously reduce contention
        if (isOpen && (bid.price > latestBid.price)) {
            synchronized (lock) {
                if (isOpen && (bid.price > latestBid.price)) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        synchronized (lock) {
            isOpen = false;
            return latestBid;
        }
    }
}
