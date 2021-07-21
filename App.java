package Mouse;
import java.util.*;
//Assignment 2
interface User{
    public void printRewards();
    public void printDetails();
}
class Cart{
    public void setItemsinthecart(int itemsinthecart) {
        this.itemsinthecart = itemsinthecart;
    }

    private HashMap<Item,Integer> itemsincart = new HashMap<>();

    public void setRestaurents(Restaurents restaurents) {
        this.restaurents = restaurents;
    }

    public Restaurents getRestaurents() {
        return restaurents;
    }

    private Restaurents restaurents;

    public float getPriceofcart() {
        return priceofcart;
    }
    public boolean checkifCartEmpty(){
        return itemsincart.size()==0;
    }
    public int getTotalItems() {
        return itemsinthecart;
    }

    public void setPriceofcart(float priceofcart) {
        this.priceofcart = priceofcart;
    }

    private int itemsinthecart;
    private float priceofcart = 0;
    Cart(){

    }
    public void addItem(Item thisitem,int quantity){
        itemsincart.put(thisitem,quantity);
    }
    public HashMap<Item,Integer> getItemsincart() {
        return itemsincart;
    }

    public void setItemsincart(HashMap<Item,Integer> itemsincart) {
        this.itemsincart = itemsincart;
    }
    public float checkforDiscount(Restaurents restaurents){
        priceofcart = restaurents.addRestaurentDiscount(priceofcart);
        return priceofcart;
    }
    public void remove(Item item){
        itemsincart.remove(item);
    }
    public float ComputePriceofCart(){
        float price = 0;
        for(Map.Entry i:itemsincart.entrySet()){
            Item temp = (Item) i.getKey();
            int quan = (int) i.getValue();
            price+=(temp.getPrice()*quan)*(float)(100-temp.getDiscount())/100;
            this.itemsinthecart+=quan;
        }
        this.priceofcart = price;
        priceofcart = this.checkforDiscount(restaurents);

        return priceofcart;
    }
    public void printInfo(){
        for(Map.Entry i:itemsincart.entrySet()){
            Item temp = (Item) i.getKey();
            int quan = (int) i.getValue();
            System.out.println(temp.getId()+" "+restaurents.getName()+" - "+temp.getPrice()+" - "+quan+" - "+temp.getDiscount());
        }
    }
}


class Customers implements User{
    protected float wallet;
    protected int rewardspoints;
    protected final String name;
    protected final String address;

    public Cart get_cart() {
        return _cart;
    }

    protected Cart _cart;
    protected String type="";

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    protected int deliveryCharge;

    public int getTotalDeliveryChargesPaid() {
        return totalDeliveryChargesPaid;
    }

    protected int totalDeliveryChargesPaid = 0;
    //ArrayList<HashMap<Item,Integer>> bought = new ArrayList<>();
    ArrayList<ArrayList<Item>> list = new ArrayList<>();
    ArrayList<ArrayList<Integer>> quantitylst = new ArrayList<>();
    ArrayList<Restaurents> res = new ArrayList<>();

    Customers(String name,String address){
        this.name = name;
        this.address = address;
        this._cart = new Cart();
        this.wallet = 1000;
        this.deliveryCharge = 40;
    }
    public void printRecentOrders(){
        Collections.reverse(list);
        Collections.reverse(quantitylst);
        Collections.reverse(res);
        for(int i=0;i<list.size();++i){
            for(int j=0;j<list.get(i).size();++j){
                System.out.println(" Bought Item: "+list.get(i).get(j).getName()+" quantity: "+quantitylst.get(i).get(j)+" for price Rs "+
                        list.get(i).get(j).getPrice()+" from Restaurant "+res.get(i).getName()+" and delivery charge "+deliveryCharge);
            }
            System.out.println();
        }
        Collections.reverse(list);
        Collections.reverse(quantitylst);
        Collections.reverse(res);
    }
    public float getWallet() {
        return wallet;
    }
    public String getName() {
        return name;
    }
    public int getRewardspoints() {
        return rewardspoints;
    }
    public String printType(){
        return type;
    }
    public void trytoAdd(int id,int quantity,Restaurents restaurents){
        //check if you have items from other restaurents in cart;
        //get item through id
        //check if sufficient items available;
        //update quantity left
        //add to cart
    	boolean ispresent = restaurents.isPresent(id);
    	if(ispresent==false) {
    		System.out.println("We don't sell this item");
    	}
    	else {
        if (restaurents.checkAvailable(id, quantity)) {
            Item item = restaurents.getItembyID(id);
            if(item.getQuantity()>=quantity) {
            	_cart.addItem(item,quantity);
            	_cart.setRestaurents(restaurents);
            	System.out.println("Items were successfully added to cart");
            }
            else {
            	System.out.println("Sorry we can't add the items you want");
            }

        }
        else {
        	System.out.println("Sorry we are short of items you want");
        	}
        }
    }
    public boolean proceedtoCheckout(){
        System.out.println("Items in cart: ");
        if(_cart.checkifCartEmpty()){
            System.out.println("Your cart is empty pls try again");
            return false;
        }
        else {
            _cart.printInfo();
            _cart.ComputePriceofCart();
            printDeliveryCharge();
            float newprice = deliveryandcustomerdiscount(_cart.getPriceofcart());
            System.out.println("Total order value = " + newprice);
            _cart.setPriceofcart(0);
            _cart.setItemsinthecart(0);
            return true;
        }
    }
    public float deliveryandcustomerdiscount(float price){
        price+=deliveryCharge;
        return price;
    }
    public void printDeliveryCharge(){
        System.out.println("Delivery charge = "+getDeliveryCharge()+"/-");
    }
    public void removeItemfromCart(Item item){
        _cart.remove(item);
    }
    public boolean buyItems(){
        //deduct money from cart;
        //add reward points to Customer and Restaurent;
        //add the list to bought items
        //clear the list of items from the cart;
        //remove the purchased items from the restaurant through cart
        _cart.ComputePriceofCart();
        float newprice = deliveryandcustomerdiscount(_cart.getPriceofcart());
        if(wallet+rewardspoints>=newprice) {
            System.out.println(_cart.getTotalItems() + " items were successfully bought for " + newprice);
            if(rewardspoints>0){
                float val =  rewardspoints - newprice;
                if(val<0){
                    rewardspoints = 0;
                    wallet += val;
                }
                else{
                    rewardspoints-=newprice;
                }
            }
            else{
                wallet-=newprice;
            }
            Restaurents ordered = _cart.getRestaurents();
            res.add(ordered);
            _cart.getRestaurents().addToBill(_cart.getPriceofcart());
            if(quantitylst.size()==10){
                quantitylst.remove(0);
                list.remove(0);
                res.remove(0);
            }
            HashMap<Item,Integer> temp = new HashMap<>();
            temp = _cart.getItemsincart();

            ArrayList<Item> temp1 = new ArrayList<>();
            ArrayList<Integer> temp2 = new ArrayList<>();
            for(Map.Entry i:temp.entrySet()){
                temp1.add((Item) i.getKey());
                temp2.add((Integer) i.getValue());
                _cart.getRestaurents().getItembyID(((Item) i.getKey()).getId()).setQuantity(_cart.getRestaurents().getItembyID(((Item) i.getKey()).getId()).getQuantity()-(Integer) i.getValue());
            }
            list.add(temp1);
            quantitylst.add(temp2);
            totalDeliveryChargesPaid+=getDeliveryCharge();
            _cart.getRestaurents().setNumofOrders(_cart.getRestaurents().getNumofOrders()+1);
            int val = _cart.getRestaurents().addRewardPoints(_cart.getPriceofcart());
            rewardspoints += val;
            _cart.setRestaurents(null);
            _cart.getItemsincart().clear();
            _cart.setPriceofcart(0);
            _cart.setItemsinthecart(0);
            return true;
        }
        else{
            System.out.println("Insufficient funds available");
            System.out.println("Input id of item to remove items");
            return false;
        }
    }

    @Override
    public void printRewards() {
        System.out.println("Reward points: "+rewardspoints);
    }

    @Override
    public void printDetails() {
        System.out.println(name+printType()+" from "+address+" has a balance of "+wallet+" in his wallet and "+rewardspoints+" reward points");
    }
}
class EliteCustomers extends Customers{
    EliteCustomers(String name,String address){
        super(name,address);
        super.type = "Elite Customer";
        super.deliveryCharge = 0;
    }

    @Override
    public float deliveryandcustomerdiscount(float price){
        if(price>200)
            price-=50;
        price+=deliveryCharge;
        return price;
    }

    //Note this override is just for me printing the types, kindly just ignore it
    @Override
    public String printType(){
        return "("+super.type+")";
    }
}
class SpecialCustomers extends Customers{
    SpecialCustomers(String name,String address){
        super(name,address);
        super.type = "Special Customer";
        super.deliveryCharge = 20;
    }

    @Override
    public float deliveryandcustomerdiscount(float price){
        if(price>200)
            price-=25;
        price+=deliveryCharge;
        return price;
    }

    //Note this override is just for me printing the types, kindly just ignore it
    @Override
    public String printType(){
        return "("+super.type+")";
    }
}
class Restaurents implements User{
    protected ArrayList<Item> items = new ArrayList<>();
    protected final String name;
    protected final String address;
    protected HashMap<Integer,Item> hmaps= new HashMap<>();
    protected int rewardPoints = 0;

    public float getBill() {
        return bill;
    }

    protected float bill = 0;

    public int getNumofOrders() {
        return numofOrders;
    }

    public void setNumofOrders(int numofOrders) {
        this.numofOrders = numofOrders;
    }

    protected int numofOrders = 0;

    public void addToBill(float val){
        bill += val;
    }

    public int getDiscount() {
        return discount;
    }

    protected int discount = 0;

    public String getType() {
        return type;
    }

    protected String type = "";
    Restaurents(String name,String address){
        this.name = name;
        this.address = address;
    }
    public void addItem(Item item){
        items.add(item);
        hmaps.put(item.getId(),item);
        System.out.println(item.getId()+" "+item.getName()+" "+item.getPrice()+" "+item.getQuantity()+" "+item.getDiscount()+"% off "+item.getCategory());
    }
    public void editItems(int id,String s,int quer){
        if(quer== 1)
            hmaps.get(id).setName(s);
        if(quer == 2)
            hmaps.get(id).setPrice(Integer.parseInt(s));
        if(quer == 3)
            hmaps.get(id).setQuantity(Integer.parseInt(s));
        if(quer == 5)
            hmaps.get(id).setDiscount(Integer.parseInt(s));
        if(quer== 4)
            hmaps.get(id).setCategory(s);

        Item temp = hmaps.get(id);
        System.out.println(temp.getId()+" "+ name+" - "+temp.getName()+" "+temp.getPrice()+" "+temp.getQuantity()+" " +temp.getDiscount()+"% "+temp.getCategory());
    }
    public void setDiscount(int discount){
        //you can't set discount here...
        System.out.println("Sorry you can't set discounts in normal restaurants");
    }
    public void displayItems(){
        for(Item i: items){
            System.out.println(i.getId()+" "+ name+" - "+i.getName()+" "+i.getPrice()+" "+i.getQuantity()+" " +i.getDiscount()+"% "+i.getCategory());
        }
    }
    public String printType(){
        return type;
    }
    public boolean checkAvailable(int id,int quantity){
        //check
        if(hmaps.get(id).getQuantity()>=quantity){
            return true;
        }
        return false;
    }
    public Item getItembyID(int id){
        return hmaps.get(id);
    }
    public boolean isPresent(int id){
        return hmaps.containsKey(id);
    }
    public float addRestaurentDiscount(float price){
        return price;
    }
    public int addRewardPoints(float val){
        int pointsadded = ((int)val/100)*5;
        rewardPoints+= pointsadded;
        return pointsadded;
    }

    @Override
    public void printRewards() {
        System.out.println("Rewards points: " +rewardPoints);
    }

    @Override
    public void printDetails() {
        System.out.println(name+printType()+ " from "+address+" has total "+ numofOrders+ " orders and has a bill value of "+bill);
    }
    public String getName() {
        return name;
    }
    //helper function ignore pls


}
class FastfoodRestaurents extends Restaurents{
    FastfoodRestaurents(String name,String address){
        super(name,address);
        super.type = "FastFood";

    }
    @Override
    public int addRewardPoints(float val){
        int pointsadded = ((int)val/150)*10;
        rewardPoints+= pointsadded;
        return pointsadded;
    }
    //since there is dynamic polymorphism this method is needed
    @Override
    public void setDiscount(int discount){
        super.discount = discount;
        System.out.println(super.discount+" % of discount set");
    }
    @Override
    public float addRestaurentDiscount(float price){
        return price*(100-discount)/100;
    }
    //ignore this override, meant for printing purpose only of restaurent type
    @Override
    public String printType() {
        return "("+super.type+")";
    }
}
class AuthenticRestaurents extends Restaurents{
    AuthenticRestaurents(String name,String address){
        super(name,address);
        super.type = "Authentic";
    }
    @Override
    public int addRewardPoints(float val){
        int pointsadded = ((int)val/200)*25;
        rewardPoints+=pointsadded;
        return pointsadded;
    }
    @Override
    public float addRestaurentDiscount(float price){
        price = price*(100-discount)/100;
        if(price>100){
            price = price-50;
        }
        return price;
    }


    //since dynamic polymorphism is being used this method is needed.
    @Override
    public void setDiscount(int discount){
        super.discount = discount;
        System.out.println(super.discount+" % of discount set");
    }

    //ignore this override,its for printing purpose only of restaurant type
    @Override
    public String printType() {
        return "("+super.type+")";
    }

}


class FoodTechCompany{
    //polymorphism
    private Restaurents[] restaurentlst;
    private Customers[] customerlst;
    private float balance;
    private int totaldeliverycharges;
    FoodTechCompany(Restaurents[] arr1,Customers[] arr2){
        this.restaurentlst = arr1;
        this.customerlst = arr2;
    }

    public void printDetailscallCustomer(int num){
        //declared tyoe is user but actual type customer
        //feel the polymorphism
        //here I call a method in the interface
        User[] coslist = new Customers[5];
        System.arraycopy(customerlst,0,coslist,0,5);
        coslist[num].printDetails();
    }
    public void printDetailscallRestaurent(int val){
        //declared tyoe is user but actual type customer
        //feel the polymorphism
        //here I call a method in the interface
        User[] roslist = new Restaurents[5];
        System.arraycopy(restaurentlst, 0, roslist, 0, 5);
        roslist[val].printDetails();
    }
    public void printRewardsforCustomer(int num){
        User[] coslist = new Customers[5];
        System.arraycopy(customerlst,0,coslist,0,5);
        coslist[num].printRewards();
    }
    public void printRewardsforRestaurant(int val){
        User[] roslist = new Restaurents[5];
        System.arraycopy(restaurentlst, 0, roslist, 0, 5);
        roslist[val].printRewards();
    }
    public void computeCompanyBalance(){
        float balance = 0;
        for(Restaurents restaurents:restaurentlst){
            balance+=restaurents.getBill();
        }
        balance*=0.01;
        this.balance = balance;
        System.out.println("Total company balance: "+balance);
    }
    public void printDeliveryCharges(){
        int paid = 0;
        for(Customers customers:customerlst){
            paid+=customers.getTotalDeliveryChargesPaid();
        }
        this.totaldeliverycharges = paid;
        System.out.println("Total delivery charges collected: "+paid);
    }

}
class Item{
    private final int id;

    private String name;
    private int price;
    private int quantity;
    private int discount;
    private String category;

    Item(String name,int price,int quantity,String category,int discount){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.discount = discount;
        this.id = helperid.val;
        this.updateval();
    }
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getDiscount() {
        return discount;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public int getId() {
        return id;
    }
    private void updateval(){
        helperid.val++;
    }

}
class helperid{
    public static int val = 1;
}

public class App {
    public static void main(String[] args) {
        Restaurents[] listofRestaurents = new Restaurents[5];
        Customers[] listofCustomers = new Customers[5];


        AuthenticRestaurents shah = new AuthenticRestaurents("Shah","Pune");
        Restaurents ravi = new Restaurents("Ravi","Pune");
        Restaurents TheChinese = new Restaurents("The Chinese","Pune");
        FastfoodRestaurents Wang = new FastfoodRestaurents("Wang","Pune");
        Restaurents Paradise = new Restaurents("Paradise","Pune");
        //you can see the actual type is hidden from restaurant list.
        listofRestaurents[0] = shah;
        listofRestaurents[1] = ravi;
        listofRestaurents[2] = TheChinese;
        listofRestaurents[3] = Wang;
        listofRestaurents[4] = Paradise;
        //you can see the declared type as restaurant and actual type different...
        EliteCustomers Ram = new EliteCustomers("Ram","Pune");
        EliteCustomers Sam = new EliteCustomers("Sam","Pune");
        SpecialCustomers Tim = new SpecialCustomers("Tim","Pune");
        Customers Kim = new Customers("Kim","Pune");
        Customers Jim = new Customers("Jim","Pune");
        //actual type is hidden in the list, declared type and actual type are different...
        listofCustomers[0] = Ram;
        listofCustomers[1] = Sam;
        listofCustomers[2] = Tim;
        listofCustomers[3] = Kim;
        listofCustomers[4] = Jim;


        //creating a food tech company, it shows polymorphism in interface
        //User[] listofrestaurents = new Restaurants[5];
        FoodTechCompany foodTechCompany = new FoodTechCompany(listofRestaurents,listofCustomers);

        boolean flag = true;
        Scanner in = new Scanner(System.in);
        while(flag){
            System.out.println("Welcome to Zomato: ");
            System.out.println("\t1) Enter as Restaurant owner ");
            System.out.println("\t2) Enter as Customer ");
            System.out.println("\t3) Check User Details ");
            System.out.println("\t4) Company Account Details ");
            System.out.println("\t5) Exit ");
            int n = in.nextInt();
            boolean missedflag = n>=1 && n<=5;
            while (missedflag) {
                if (n == 1) {
                    System.out.println("Choose Restaurant");
                    for(int i=0;i<5;++i){
                        System.out.println("\t"+(i+1)+") "+ listofRestaurents[i].getName()+listofRestaurents[i].printType());
                    }
                    int ne = in.nextInt();
                    boolean flag2 = ne >= 1 && ne <= 5;
                    ne--;
                    while (flag2) {
                        System.out.println("Welcome " + listofRestaurents[ne].getName());
                        System.out.println("\t1) " + "Add Item");
                        System.out.println("\t2) " + "Edit Item");
                        System.out.println("\t3) " + "Print Rewards");
                        System.out.println("\t4) " + "Discount on Bill value");
                        System.out.println("\t5) " + "Exit");
                        int nex = in.nextInt();
                        boolean flag3 = nex >= 1 && nex <= 5;
                        while (flag3) {
                            if (nex == 1) {
                                String name = in.next();
                                int price = in.nextInt();
                                int quantity = in.nextInt();
                                String type = in.next();
                                int offer = in.nextInt();
                                Item item = new Item(name, price, quantity, type, offer);
                                listofRestaurents[ne].addItem(item);
                                flag3 = false;
                            }
                            if (nex == 2) {
                                listofRestaurents[ne].displayItems();
                                int next = in.nextInt();
                                boolean flag4 = true;
                                while (flag4) {
                                    System.out.println("Choose an attribute to edit");
                                    System.out.println("\t1) Name");
                                    System.out.println("\t2) Price");
                                    System.out.println("\t3) Quantity");
                                    System.out.println("\t4) Category");
                                    System.out.println("\t5) Offer");
                                    int nextn = in.nextInt();
                                    boolean flag5 = nextn >= 1 && nextn <= 5;
                                    while (flag5) {
                                        String str = in.next();
                                        listofRestaurents[ne].editItems(next, str, nextn);
                                        flag5 = false;
                                    }
                                    flag4 = false;
                                    flag3 = false;
                                }
                            }
                            if (nex == 3) {
                                foodTechCompany.printRewardsforRestaurant(ne);
                                flag3 = false;
                            }
                            if (nex == 4) {
                                int discount = in.nextInt();
                                listofRestaurents[ne].setDiscount(discount);
                                flag3 = false;
                            }
                            if (nex == 5) {
                                flag3 = false;
                                missedflag = false;
                                flag2 = false;

                            }
                        }
                    }

                }
                if(n==2){
                    for(int i=0;i<5;++i){
                        System.out.println("\t"+(i+1)+". "+listofCustomers[i].getName()+listofCustomers[i].printType());
                    }
                    int p = in.nextInt();
                    boolean flag5 = p>=1 && p<=5;
                    p--;//customer number
                    while (flag5){
                        System.out.println("Welcome "+listofCustomers[p].getName());
                        System.out.println("Customer menu ");
                        System.out.println("1) Select Restaurent");
                        System.out.println("2) Checkout Cart");
                        System.out.println("3) Reward won");
                        System.out.println("4) Print the recent orders");
                        System.out.println("5) Exit");
                        int piz = in.nextInt();
                        boolean flag6 = piz>=1 && piz<=5;
                        while (flag6){
                            if(piz == 1){
                                System.out.println("Choose Restaurant");
                                for(int i=0;i<5;++i){
                                    System.out.println("\t"+(i+1)+") "+ listofRestaurents[i].getName()+listofRestaurents[i].printType());
                                }
                                int pizz = in.nextInt();//this is restaurant number
                                boolean flag7 = pizz>=1 && pizz<=5;
                                pizz--; //using this from the array soo...
                                while (flag7){
                                    listofRestaurents[pizz].displayItems();
                                    int pizza = in.nextInt(); //selected item
                                    int pizzah = in.nextInt(); //enter quantity;
                                    listofCustomers[p].trytoAdd(pizza,pizzah,listofRestaurents[pizz]);
                                    flag6 = false;
                                    flag7 = false;
                                }

                            }
                            if(piz == 2){
                                if(!listofCustomers[p].proceedtoCheckout()){
                                };
                                System.out.println("\t1) Proceed to checkout");
                                System.out.println("\t2) If you cart was empty press 2 so you could go back");
                                int pizzahu = in.nextInt();
                                if(pizzahu==1){
                                    boolean check = listofCustomers[p].buyItems();
                                    if(!check){
                                        int input = in.nextInt();
                                        listofCustomers[p].removeItemfromCart(listofCustomers[p].get_cart().getRestaurents().getItembyID(input));
                                        System.out.println("Item removed please go back to cart to checkout value");
                                    }
                                }
                                if(pizzahu==2){

                                }
                                flag6 = false;
                            }
                            if (piz==3){
                                foodTechCompany.printRewardsforCustomer(p);
                                flag6 = false;

                            }
                            if (piz==4){
                                listofCustomers[p].printRecentOrders();
                                flag6 = false;
                            }
                            if (piz==5){
                                flag5 = false;
                                missedflag = false;
                                flag6 = false;
                            }
                        }
                    }
                }
                if(n==3){
                    System.out.println("1) Customer list");
                    System.out.println("2) Restaurant list");
                    int d = in.nextInt();
                    if(d==1){
                        for(int i=0;i<5;++i){
                            System.out.println((i+1)+". "+listofCustomers[i].getName());
                        }
                        int dom = in.nextInt();
                        boolean flag7 = dom>=1 && dom<=5;
                        dom--;
                        while (flag7){
                            foodTechCompany.printDetailscallCustomer(dom);
                            missedflag = false;
                            flag7 = false;
                        }
                    }
                    if(d==2){
                        for(int i=0;i<5;++i){
                            System.out.println((i+1)+". "+listofRestaurents[i].getName());
                        }
                        int domi = in.nextInt();
                        boolean flag8 = domi>=1 && domi<=5;
                        domi--;
                        while (flag8){
                            foodTechCompany.printDetailscallRestaurent(domi);
                            missedflag = false;
                            flag8 = false;
                        }
                    }
                }
                if(n==4){
                    foodTechCompany.computeCompanyBalance();
                    foodTechCompany.printDeliveryCharges();
                    missedflag = false;
                }
                if(n==5){
                    flag = false;
                    missedflag = false;

                }
            }
        }
    }
}
