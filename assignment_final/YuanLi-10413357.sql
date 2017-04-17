/* ****************************** first report******************************* */
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



/* ****************************** second report******************************* */


SELECT
	q.cust AS CUST,
	q.prod AS PROD,
	q.CT_MAX,
	q."month" AS CT_MONTH,
	q."day" AS CT_DAY,
	q."year" AS CT_YEAR,
	q.NY_MIN,
	q.NY_MONTH,
	q.NY_DAY,
	q.NY_YEAR,
	k.NJ_MIN,
	k."month" AS NJ_MONTH,
	k."day" AS NJ_DAY,
	k."year" AS NJ_YEAR
FROM
	(
		SELECT
			COALESCE (i.cust, j.cust) AS CUST,
			COALESCE (i.prod, j.prod) AS PROD,
			i.CT_MAX,
			i."month",
			i."day",
			i."year",
			j.NY_MIN,
			j."month" AS NY_MONTH,
			j."day"  AS NY_DAY,
			j."year" AS NY_YEAR
		FROM
			(
				(
					SELECT
						C .cust,
						A .prod,
						C . MAX AS CT_MAX,
						"month",
						"day",
						"year"
					FROM
						sales AS A
					JOIN (
						SELECT
							prod,
							cust,
							MAX (quant)
						FROM
							sales
						WHERE
							"state" = 'CT'
						AND "year" BETWEEN 2000
						AND 2005
						GROUP BY
							prod,
							cust
					) AS C ON A .prod = C .prod
					AND A .quant = C . MAX
				) AS i
				FULL OUTER JOIN (
					SELECT
						b.cust,
						A .prod,
						b. MIN AS NY_MIN,
						"month",
						"day",
						"year"
					FROM
						sales AS A
					JOIN (
						SELECT
							prod,
							cust,
							MIN (quant)
						FROM
							sales
						WHERE
							"state" = 'NY'
						GROUP BY
							prod,
							cust
					) AS b ON A .prod = b.prod
					AND A .quant = b. MIN
				) AS j ON i.cust = j.cust
				AND i.prod = j.prod
			)
	) AS q
FULL OUTER JOIN (
	SELECT
		b.cust,
		A .prod,
		b. MIN AS NJ_MIN,
		"month",
		"day",
		"year"
	FROM
		sales AS A
	JOIN (
		SELECT
			prod,
			cust,
			MIN (quant)
		FROM
			sales
		WHERE
			"state" = 'NJ'
		GROUP BY
			prod,
			cust
	) AS b ON A .cust = b.cust
	AND A .prod = b.prod
	AND A .quant = b. MIN
) AS K ON q.cust = K .cust
AND q.prod = K .prod
ORDER BY
	q.cust,
	q.prod


