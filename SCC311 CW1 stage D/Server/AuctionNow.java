import java.io.Serializable;

public class AuctionNow implements java.io.Serializable{
    private AuctionItem Item = new AuctionItem();

    private int userID;
    // private int itemID;
    private int reservePrice;
    private int highestBidderID;

    public AuctionNow(int itemID, AuctionSaleItem saleItem, int userID)
    {
        this.Item.name = saleItem.name;
        this.Item.description = saleItem.description;
        this.reservePrice = saleItem.reservePrice;
        this.Item.highestBid = 0;
        this.Item.itemID = itemID;
        this.userID = userID;
    }

    public void setBidder(int bidderID)
    {
        this.highestBidderID = bidderID;   
    }

    public int getBidderID()
    {
        return this.highestBidderID;
    }

    public AuctionItem getAuctionItem()
    {
        return this.Item;
    }

    public void setBid(int price)
    {
        this.Item.highestBid = price;
    }

    public int getReservePrice()
    {
        return this.reservePrice;
    }

    public int getUserID()
    {
        return this.userID;
    }
}


    // public int getItemID()
    // {
    //     return this.Item.itemID;
    // }

    // public String getName()
    // {
    //     return this.name;
    // }

    // public String getDes()
    // {
    //     return this.description;
    // }

    // public int getBid()
    // {
    //     return this.highestBid;
    // }