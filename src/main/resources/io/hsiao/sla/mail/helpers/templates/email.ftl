<head>
<meta charset="UTF-8">
<style>
  p {font-family:Arial; font-size:11pt; color:black;}
  table,th,td {font-family:Arial; border:1px solid black;}
  th {font-size:11pt; font-weight:bold; color:black; background-color:#00CC66; padding:5pt;}
  td {font-size:10pt; color:black; padding:2pt;}
  p.signature1 {font-family:Tahoma Arial; font-size:11pt; color:gray; margin-bottom:0px;}
  p.signature2 {font-family:Tahoma Arial; font-size:11pt; color:gray; margin-top:5px; font-weight:bold;}
  span.warning {font-size:11pt; font-weight:bold; color:#FF3300;}
  span.logo {color:#0000A0; font-weight:bold;}
  span.tagline {color:#0000A0; font-size:9pt; font-weight:bold; font-style:italic;}
  table.summary {font-family:Arial; border:0px; padding-left:20pt;}
  td.summary {font-size:10pt; color:black; border:0px;}
  td.breakdown {width:35pt; color:black; text-align:center;}
</style>
</head>
<body>
<p>Dear all</p>
<p>
  This email is a reminder in regard to the <i>service-level agreement</i> standards adopted company-wide.
</p>
<p>
  Please check the current progress and <span class="warning">pay special attention to overdue artifacts</span>.
</p>
<table class="summary">
  <tr>
    <td class="summary">- Total outstanding defect(s):</td>
    <td class="summary">#${cntTotalDefects}</td>
  </tr>
  <tr>
    <td class="summary">- Overdue defect(s):</td>
    <td class="summary">#${cntOverdueDefects}</td>
  </tr>
  <tr>
    <td class="summary">- Expire within 24 hours defect(s):</td>
    <td class="summary">#${cnt24HExpireDefects}</td>
  </tr>
  <tr>
    <td class="summary">- Defect(s) priority breakdown:</td>
    <td class="breakdown" style="background-color:#FF0000">#${cntP1Defects}</td>
    <td class="breakdown" style="background-color:#CC0033">#${cntP2Defects}</td>
    <td class="breakdown" style="background-color:#CC9933">#${cntP3Defects}</td>
    <td class="breakdown" style="background-color:#FFCC66">#${cntP4Defects}</td>
  </tr>
</table>
<br>
<table>
  <tr>
  <#list headlines as headline><#t>
    <th>${headline}</th>
  </#list><#t>
  </tr>
  <#list artifacts as artifact><#t>
  <tr>
    <#list artifact as field><#t>
    <#if field == "1-Highest"><td style="background-color:#FF0000; text-align:center">${field}</td>
    <#elseif field == "2-High"><td style="background-color:#CC0033; text-align:center">${field}</td>
    <#elseif field == "3-Medium"><td style="background-color:#CC9933; text-align:center">${field}</td>
    <#elseif field == "4-Low"><td style="background-color:#FFCC66; text-align:center">${field}</td>
    <#elseif field?matches("\\Aartf\\d+\\Z")><td><a href="${artifactBaseURL}/${field}">${field}</a></td>
    <#elseif field?matches("\\AOverdue\\s+\\d+(?:\\.\\d+)?\\s+workday.+\\Z")><td style="font-weight:bold; color:#FF3300">${field}</td>
    <#else><td>${field}</td>
    </#if><#t>
    </#list><#t>
  </tr>
  </#list><#t>
</table>
<p class="signature1">Best Regards</p>
<p class="signature2">SLA Mailing Robot | Your Department | <span class="logo">Your Company</span> <span class="tagline">your tagline</p>
</body>
