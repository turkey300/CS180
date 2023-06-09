# Marketplace
### By Dimitri Paikos, Tyler Kei, Ryan Timmerman, and Ekaterina Tszyao
#### Instructions
To compile and run the program, all files must be the same way as they were in GitHub/Vocarium. 
Additionally, all the java files must be compiled. 
The Market file is the "client" so the users should run this program to access the marketplace.
Similarly, the Server file is the server, and needs to be running for the client to connect to it.
Finally, check Tests.md for the tests ran on the program. Replicating these tests should return the exact same results
as described in the file.
#### Submission
Tyler - Submitted report on Brightpace. Kate - Submitted presentation. Dimitri - Submitted Vocarium workspace.
#### Market Class
The market class contains all the logic for the client. Because of this, it is closely tied to the server class, 
often communicating to and from it. It displays the marketplace, and communicates to the server what the user has 
selected, whether that be purchasing a product, viewing the dashboard, exporting or importing products, and more. 
This code was tested by going through all the options and making sure every option and edge case worked as expected.
#### Server Class
The server class contains all the logic for the server. It must be running for clients to connect from the market class.
Because of this, it is closely tied to all the other classes, frequently calling them and communicating with the market 
class. This class contains the logic to purchase products, add users, and everything that needs to be done on the 
backend. This code was tested by testing all its functions through the market class and ensuring everything worked as 
expected.
#### Customer Class
The customer class contains all the functionality for the customer user. This has all the fields a customer would need,
like their username, password, purchase history, etc. Additionally, it has all the logic to save the user to a file,
that way it is saved when the user exits. We tested this code by itself, making sure it would successfully save and 
read customers, and then we made sure it worked when connected to the other classes by testing all the functions of the 
program. This class is mainly connected to the market class as the market class contains the logic/terminal interface.
Additionally, it is connected to the purchase history and shopping cart because it contains an array of these objects
to save the customers purchase history and shopping cart.
#### Seller Class
The seller class contains all the functionality for the seller user. This has all the fields a seller would need,
like their username, password, store list, etc. Additionally, it has all the logic to save the user to a file,
that way it is saved when the user exits. We tested this code by itself, making sure it would successfully save and
read sellers, and then we made sure it worked when connected to the other classes by testing all the functions of the
program. This class is mainly connected to the market class as the market class contains the logic/terminal interface.
Additionally, it is extremely connected to the store and product class. The seller contains an array of all the 
stores it has, and the stores hold all the products, so when a store/product is updated it is crucial that the 
respective seller is updated.
#### Store Class
The store class contains all the details and functions for a store. This includes store name, the seller it is under,
products, etc. Mainly, it contains the logic to add/remove products and purchase products. We tested the functionality 
by creating a store and testing its functions by itself, and then made sure it worked with the rest of the code. This 
class is closely tied to the seller and product class because the seller has a list of stores and each store has a list
of products it contains.
#### StoreComparatorByProductsSold Class
This class is a simple class which compares two stores by the amount of products sold by each of the stores. By nature,
it is closely tied to the store and product class.
#### Product Class
This class contains all the logic for products. This includes the price, description, amount, etc. Mainly, it contains
the logic for editing and purchasing a product. The code was tested by itself by adding a new product and then modifying 
it, and was tested again when we connected it to the other classes. When a product is purchased, it removes the amount 
purchased and increases the amount sold. This is closely tied to the store class because the store class has an array 
of all the products it contains. Additionally, the product contains the store it is under which helped out in the 
market code when trying to save the seller when a product was modified.
#### ProductComparatorByAvailability Class
This class compared two products by the availability. It is a subset of the product class.
#### ProductComparatorByPrice
This class compared the price of two products. It is a subset of the product class.
#### ShoppingCart Class
This class contained the shopping cart. It contained the product in the shopping cart, the seller who has the product,
and the amount in the shopping cart. Mainly, it contains the logic to check if the item in the shopping cart is still 
valid (it may have been deleted or out of stock). We tested this code by adding a product to cart and then editing the
shopping cart, and made sure it worked with the code in market. This class is closely connected to the product and 
seller class because it holds a product and needs to know what seller has the item to check if the item is still valid 
later.
#### PurchaseHistory Class
This class is very similar to the shopping cart class. It contains the product that was purchased and the amount 
purchased. We tested this by purchasing a product and then checking if it would show up in the class. Additionally, we 
made sure it worked when used in the market code. This is connected to the product class because it contains a product
and the customer class because every customer has a purchase history.
#### AlreadyUserException Class
This exception is thrown when a seller/customer account is being made but there already is a customer/seller under that
username.
#### NoUserException Class
This exception is thrown when someone tries to log in but the account cannot be found.
#### OtherUserException
This exception is thrown when someone is trying to create a seller/customer but that username is already taken by the 
opposite user (ex. seller creates "user1" but there already is a customer named "user1").
