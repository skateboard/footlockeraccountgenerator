<h1 align="center">Footlocker Account Generator</h1>
<div align="center">
	<strong>An open sourced footlocker account generator</strong>
</div>
<br />

# Example Config
```json
{  
  "catch_all": "example.com"  
}
```

# Requirements
[Gmail API](https://developers.google.com/gmail/api) is required to get activation emails. You must put the ```credentials.json``` in the same folder as the program.

# Usage
To use go to [Latest Release](https://github.com/skateboard/footlockeraccountgenerator/releases) and download.
once downloaded make sure you have a config in the same folder called ```config.json``` with the config format as seen above. Simply run this

to use proxies put a ```proxies.txt``` in the same folder as the program
```
java -jar footlockeraccountgenerator.jar
```
and follow the steps it asks for
