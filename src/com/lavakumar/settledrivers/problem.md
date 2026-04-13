A. Settle Driver Trips / Driver Payroll / Delivery Cost Dashboard
Base version:
You have drivers with hourly rates. Deliveries arrive as completed intervals (driverId, startTime, endTime). Build an in-memory service with methods like addDriver, recordDelivery, and getTotalCost. The dashboard wants the total cost of all completed deliveries. Overlapping deliveries for the same driver still count independently. Time precision is at least seconds.

Likely follow-up 1:
Add settlement semantics such as payUpToTime(t) and getUnpaidCost(). That means your design must separate accrued total from already settled total. Recent reports mention exactly this.

Likely follow-up 2:
Make getTotalCost() fast for a live dashboard. One candidate explicitly said the interviewer wanted optimized getTotalCost(), and they maintained a running total as deliveries were added.


judging criteria:

Did you clarify rounding, currency precision, inclusive/exclusive interval semantics, and whether invalid input exists?
Did you choose BigDecimal-style money handling instead of float in real code?
Did you keep getTotalCost() O(1) and avoid rescanning every delivery?
Did you model future extensions cleanly, such as unpaid balances, replay/idempotency, cancellation, or per-driver queries?
These are inferred from the reported follow-ups and from the “production-quality + good OOP + live dashboard” phrasing.