with ordered_corner_period_query as (
    select *
    from (
        select *
        from (
            select *, case
                          when lag("aggregate_sum") over (partition by "Deal") < 0 or lag("aggregate_sum") over (partition by "Deal") is null then 0
                          when (lead("aggregate_sum") over (partition by "Deal") < 0 or lead("aggregate_sum") over (partition by "Deal") is null) or "aggregate_sum" < 0 then 1
                      end corner
            from (select "Date", "Deal", "Sum",
                     sum("Sum") over (partition by "Deal" order by "Date") aggregate_sum
                  from public."PDCL") a
        ) b
    ) c
    where corner is not null)


select "Deal", sum("Sum") from public."PDCL" group by "Deal" having sum("Sum") > 0;; -- Общую (накопленную) сумму просроченного долга непогашенную (не выплаченную) к моменту расчета.

select q."Date", q."Deal" --Дата начала текущей (последней) просрочки
from (
    select *, case
			when "corner" = 0 and lead("aggregate_sum") over (partition by "Deal") > 0 then 1
		  end return_min
    from ordered_corner_period_query
) q where return_min = 1


select DATE_PART('day', current_date::timestamp - q."Date"::timestamp)::numeric::integer, q."Deal" --Кол-во дней текущей просрочки
from (
    select *, case
			when "corner" = 0 and lead("aggregate_sum") over (partition by "Deal") > 0 then 1
		  end return_min
    from ordered_corner_period_query
) q where return_min = 1
