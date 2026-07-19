const { app, BrowserWindow, screen } = require('electron')
const path = require('path')
const { pathToFileURL } = require('url')
const child_process = require('child_process')

let win
let javaProcess

app.on('ready', () => {
  const { width, height } = screen.getPrimaryDisplay().workAreaSize

  win = new BrowserWindow({
    width: width - 200,
    height: height - 100,
    center: true,
    backgroundColor: '#2b292c',
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false
    }
  })

  javaProcess = child_process.spawn(
    'java',
    ['-Dapple.awt.UIElement=true', '-jar', 'java/smudgr.jar'],
    {
      cwd: __dirname,
      stdio: 'inherit'
    }
  )

  initWindow()
})

function initWindow() {
  win.loadURL(pathToFileURL(path.join(__dirname, 'ui/index.html')).href)
  win.webContents.openDevTools()

  win.on('closed', () => {
    win = null
  })
}

app.on('window-all-closed', () => {
  if (javaProcess) {
    javaProcess.kill('SIGINT')
  }
  app.quit()
})
