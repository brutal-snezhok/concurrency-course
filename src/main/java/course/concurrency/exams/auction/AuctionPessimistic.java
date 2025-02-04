package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    public synchronized boolean propose(Bid bid) {
        if(latestBid == null) {
            latestBid = bid;
        }

        if (bid.price > latestBid.price) {
            notifier.sendOutdatedMessage(latestBid);
            latestBid = bid;
            return true;
        }

        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
