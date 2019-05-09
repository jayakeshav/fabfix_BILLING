delimiter //
create procedure insert_sales_transactions(
in email1 varchar(50),
in quantity1 int,
in movieId1 varchar(10),
in token1 varchar(50)
)
begin
  insert into sales(email, movieId, quantity, saleDate) value (email1,movieId1,quantity1,curdate());
  insert into transactions(sId, token) value ((select id from sales where email = email1 order by id desc limit 1),token1);
end //
delimiter ;