# HyperCoachInterface Mobile
## Requirements
To run the tests you should first install Android Studio and the project requirements. 
- <a href="https://docs.npmjs.com/">npm >= 7.24.1</a>
- <a href="https://nodejs.org/en/">nodejs >= 16.11.1</a>

If you don't have Android Studio installed, download the app on https://developer.android.com/studio and follow the steps. If you don't have the requirements installed you can do so by running the following commands.

```bash
sudo apt-get update
curl -fsSL https://deb.nodesource.com/setup_16.x | sudo -E bash -
sudo apt-get install -y nodejs
sudo apt-get install npm
npm install -g npm
```
For Windows installation you can follow the [Node documentation](https://nodejs.org/en/download/package-manager/) and [npm Documentation](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)

## Setting up the API url

Go to the _**src/api/api.js**_ and set up the baseUrl to your API's server url.

Once that is done you could use our own mailing.js template file.

## Project setup
```bash
npm install
```

### Starts the API.
```bash
npm run start
```

