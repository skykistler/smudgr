const electron = require('electron')
const {app, BrowserWindow} = electron

const path = require('path')
const url = require('url')
const child_process = require('child_process')

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let win

// smudgr jar
let javaProcess

app.on('ready', () => {
  const {width, height} = electron.screen.getPrimaryDisplay().workAreaSize

  win = new BrowserWindow({
    width: width - 200,
    height: height - 100,
    center: true,
    backgroundColor: '#2b292c'
  })

  javaProcess = child_process.exec(
    'java -Dapple.awt.UIElement="true" -jar java/smudgr.jar',
    {
      cwd: __dirname,
      killSignal: 'SIGINT'
  })

  setTimeout(initWindow, 1000)
})

function initWindow() {
  win.loadURL(url.format({
    pathname: path.join(__dirname, 'ui/index.html'),
    protocol: 'file:',
    slashes: true
  }))

  win.on('closed', () => {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    win = null
  })
}

// Quit when all windows are closed.
app.on('window-all-closed', () => {
  javaProcess.kill();
  app.quit()
})

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.
