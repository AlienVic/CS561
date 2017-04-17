SELECT 
	i.prod, 
	i.MAX_Q, 
	i.cust as max_cust, 
	i."month" as max_month, 
	i."day" as max_day, 
	i."year" as max_year,
	i."state" as max_st,
	j.MIN_Q, 
	j.cust as min_cust, 
	j."month" as min_month, 
	j."day" as min_day, 
	j."year" as min_year,
	j."state" as min_st, 
	CAST(i."avg" as INTEGER) as AVG_Q
FROM
	(SELECT a.prod, b.max as MAX_Q, cust, "month", "day", "year", "state" ,b.avg
	FROM sales as a 
	INNER JOIN 
	(SELECT prod, MAX(quant), AVG(quant)
							FROM sales  
							GROUP BY prod) as b
	ON a.quant = b.MAX AND a.prod = b.prod) as i

INNER JOIN

	(SELECT a.prod, c.min as MIN_Q, cust, "month", "day", "year", "state" 
	FROM sales as a
	INNER JOIN 
	(SELECT prod, MIN(quant)
								FROM sales  
								GROUP BY prod) as c
	ON a.quant = c.MIN AND a.prod = c.prod) as j

ON i.prod = j.prod
