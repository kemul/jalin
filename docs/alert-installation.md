# Installation Instructions Documentation for Alert System

# Installation Instructions for Alert System

## Prerequisites
- Ensure the transactional database is already set up with appropriate schemas.
- Verify access to New Relic and PagerDuty accounts.

## Step-by-Step Installation
1. **Database Configuration**:
   - Configure the database to handle high transaction rates.
   - Ensure logging is enabled for transaction status updates.

2. **New Relic Setup**:
   - Install and configure New Relic agents on the database servers.
   - Set up dashboards and alert conditions for failed transaction monitoring.
   - SetUp Query NewRelic for this case : 
    ```
    SELECT count(*) FROM Transaction WHERE status = 'failed' TIMESERIES 5 minutes
    ```    
    To implement this in New Relic, you would go into the Alerts section, create a new NRQL alert condition, and paste this query into the condition setup. This setup allows New Relic to continuously evaluate the query and send an alert if the condition is met.

3. **PagerDuty Integration**:
   - Configure PagerDuty services and integration keys.
   - Set up alerting rules in New Relic to trigger incidents in PagerDuty.

4. **Verification**:
   - Perform test transactions to verify alerts are triggered and logged correctly.
   - Check that PagerDuty receives and escalates alerts as configured.

## Post-Installation
- Document the installation process and any relevant configurations.
- Train the operations team on monitoring and responding to alerts.
