# SpartanDiscordBridge

Bridges the connection between DiscordSRV and Spartan AntiCheat in order to have violations logged to a log channel.

<h1>Embed</h1>

![alt embed](https://i.ibb.co/3Bm3LWh/download.png)

<h3>Colors and statuses</h3>

🟢 Negligible: Below 4 violations<br/>
🟡 Abnormal: Below 8 violations<br/>
🟠 Potential: Below 14 violations<br/>
🔴 Certain: 14 violations or above<br/>

<h1>Config:</h1>
<ul>
  <li>
    LogChannelID
    <ul><li>The ID of the log channel to send messages in.</li></ul>
  </li>
  <li>
    StaffRoleID
    <ul><li>The ID of the role to ping when fit</li></ul>
  </li>
</ul>

<h1>When does it ping?</h1>

As to not waste staff's time, it only starts pinging the staff role when the status becomes Potential or above. It will also only ping once per person, per hack, per hour.
