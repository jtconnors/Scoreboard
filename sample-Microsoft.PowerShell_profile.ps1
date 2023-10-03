#
# Sample Microsoft.PowerShell_profile.ps1 file. This will
# set the PowerShell environment at PowerShell start up.
#
# This file can be:
# o renamed to Microsoft.PowerShell_profile.ps1 and placed in the
#   User's Documents\WindowsPowerShell directory
# o edited to reflect your JDK environment
#
$env:JAVA_HOME = 'C:\devel\jdk\defaultjdk'
$env:PATH = $env:JAVA_HOME + '\bin;' + $env:PATH