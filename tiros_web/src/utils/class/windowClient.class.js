import router from '@/router'
import Vue from 'vue'
import store from '@/store'
import {
	getPlayContent,
	getPlayScreenList,
} from '@/api/tirosDispatchApi'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import { EventEmitter } from 'events'

const WindowMutation = {
	LEDLOGIN: 'LEDLOGIN',
	SCREENLIST: 'SCREENLIST',
	SCREENCONTENT: 'SCREENCONTENT',
	ROUTESCREEN: 'ROUTESCREEN',
	REFRESHTOKEN: 'REFRESHTOKEN',
}


class WindowObject {
	constructor(windowServer = undefined) {
		this.windowServer = windowServer
		this.source = null
		this.origin = null
		this.eventEmitter = new EventEmitter()
		window.addEventListener("message", this.messageEvent.bind(this), false)
	}

	get isServer() {
		return this.windowServer ? true : false
	}

	get preEmit() {
		return this.isServer ? 'CLIENT' : 'SERVER'
	}

	get preOn() {
		return this.isServer ? 'SERVER' : 'CLIENT'
	}

	messageEvent(event) {
		this.source = event.source
		this.origin = event.origin
		let data = event.data

		if (event.data['sync']) {
			this.eventEmitter.emit(data.type, data.data, (cbData) => {
				this.emit(data.sync, cbData)
			})
		} else {
			this.eventEmitter.emit(data.type, data.data)
		}
	}

	on(typeName, func) {
		this.eventEmitter.on(`${this.preOn}:${typeName}`, func)
	}

	emit(typeName, data = undefined) {
		if (this.windowServer) {
			this.windowServer.postMessage({
				type: `${this.preEmit}:${typeName}`,
				data: data
			}, '*')
		} else {
			this.source.postMessage({
				type: `${this.preEmit}:${typeName}`,
				data: data
			}, this.origin)
		}
	}

	async emitSync(typeName, data, settimeout = 20000) {
		function getTimeStr() {
			let date = new Date()
			return `${date.getFullYear()}${date.getMonth()}${date.getDay()}${date.getHours()}${date.getUTCMinutes()}${date.getUTCSeconds()}${date.getUTCMilliseconds()}`
		}
		return new Promise((resolve) => {
			let syncEventName = `${typeName}.SYNC${getTimeStr()}`
			let dataPackage = {
				type: `${this.preEmit}:${typeName}`,
				data: data,
				sync: syncEventName
			}
			let timer = null
			const eventRef = (ref) => {
				clearTimeout(timer)
				resolve(ref)
			}
			timer = setTimeout(() => {
				this.eventEmitter.removeListener(`${this.preOn}:${syncEventName}`, eventRef)
				resolve()
			}, settimeout);

			this.eventEmitter.on(`${this.preOn}:${syncEventName}`, eventRef)
			if (this.windowServer) {
				this.windowServer.postMessage(dataPackage, '*')
			} else {
				this.source.postMessage(dataPackage, this.origin)
			}
		})

	}
}

class WindowClient {
	constructor() {
		this.windowObject = new WindowObject()
		this.windowObject.on(WindowMutation.LEDLOGIN, (data, cb) =>{
			this.login(data).then(res =>{
				cb(res)
			})
		})
		this.windowObject.on(WindowMutation.ROUTESCREEN, this.routeScreen.bind(this))
		this.windowObject.on(WindowMutation.REFRESHTOKEN, this.refreshToken.bind(this))
		this.windowObject.on(WindowMutation.SCREENLIST, (data, cb) => {
			this.getLedSetting().then(setting => {
				cb({
					success: true,
					result: setting
				})
			})
		})
		this.windowObject.on(WindowMutation.SCREENCONTENT, (data, cb) => {
			this.getSettingUrl(data).then(res => {
				cb({
					success: true,
					result: res
				})
			})
		})
		this.windowObject.on('/setScreenStype', this.setScreenStype.bind(this))
	}

	async login(data) {
		document.body.classList.add('led-screen-client')
		if (data.route) {
			if (data.restLogin) {
				await store.dispatch('Logout', {}).then(( ) =>{
					this.routeScreen('/')
				})
			}
			window.ledClientUrl = data.route
			if (Vue.ls.get(ACCESS_TOKEN) && !data.restLogin) {
				this.routeScreen(data.route)
				return {
					success: true
				}
			} else {
				return store.dispatch('Login', {
					username: data.user,
					password: data.pwd
				}).then(res => {
					console.log('登录成功-web');
					console.log(res);
					if (res.success) {
						if (!data.restLogin) {
							this.routeScreen(data.route)
						}
						return {
							success: true
						}
					} else {
						return {
							success: false,
							message: res.message.message
						}
					}
				}).catch(error =>{
					return{
						success: false,
						message: error.message
					}
				})
			}
		} else {
			return {
				success: false,
				message: '登录路由未设置'
			}
		}
		
	}

	setScreenStype(data){
		if (data.homeStatus) {
			document.body.classList.remove('led-screen-client')
		}else {
			document.body.classList.add('led-screen-client')
		}
	}

	routeScreen(route) {
		if (route) {
			// route="/video/test.jpg"
			let reg = /^\/video/g
			if (reg.test(route)) {
				window.ledClientUrl = '/tiros/dispatch/video_image'
				router.push({
					path: '/tiros/dispatch/video_image',
					query: { url: route }
				})
				if (!window.ledFileUrl || window.ledFileUrl !== route) {
					setTimeout(() => {
						const event = new CustomEvent('Led_Route_video', {detail: route});
						window.dispatchEvent(event)
					}, 1000);
				}
				window.ledFileUrl = route
				
			} else {
				window.ledClientUrl = route
				window.ledFileUrl = null
				router.push(route)
			}
		} else {
			console.log('路由没设置');
			this.routeScreen('/tiros/dashboard/homepage')
		}
	}

	refreshToken(){
		store.dispatch('Refresh').then(res => {
			console.log(res)
		}).catch(err => {
			console.error(err)
		})
	}

	async getLedSetting() {
		let ledSetting = {
			led: [],
			group: []
		}
		return await Promise.all([getPlayScreenList({
			screenType: 1
		}).then(res => {
			if (res.success) {
				ledSetting.led = res.result
			}
			return res.success
		}), getPlayScreenList({
			screenType: 2
		}).then(res => {
			if (res.success) {
				ledSetting.group = res.result
			}
			return res.success
		})]).then(res => {
			return ledSetting
		})
	}

	async getSettingUrl(data) {
		return getPlayContent({
			screenId: data.id
		}).then(res => {
			if (res.success) {
				if (res.result.length) {
					return res.result[0].address
				}
				return ''
			}
		})
	}
}
const windowClient = new WindowClient()
export default windowClient


