import Menu from 'ant-design-vue/es/menu'
import Icon from 'ant-design-vue/es/icon'

const { Item, SubMenu } = Menu

export default {
    name: 'SMenu',
    props: {
        menu: {
            type: Array,
            required: true
        },
        theme: {
            type: String,
            required: false,
            default: 'dark'
        },
        mode: {
            type: String,
            required: false,
            default: 'inline'
        },
        collapsed: {
            type: Boolean,
            required: false,
            default: false
        },
        layoutMode: {
            type: String,
            required: false,
            default: ''
        }
    },
    data() {
        return {
            openKeys: [],
            selectedKeys: [],
            cachedOpenKeys: []
        }
    },
    computed: {
        rootSubmenuKeys: vm => {
            const keys = []
            vm.menu.forEach(item => keys.push(vm.getMenuKey(item)))
            return keys
        }
    },
    mounted() {
        this.updateMenu()
    },
    watch: {
        collapsed(val) {
            if (val) {
                this.cachedOpenKeys = this.openKeys.concat()
                this.openKeys = []
            } else {
                this.openKeys = this.cachedOpenKeys
            }
        },
        $route: function () {
            this.updateMenu()
        }
    },
    methods: {
        normalizeKey(value) {
            if (value === undefined || value === null) {
                return ''
            }
            return String(value)
        },
        getMenuKey(menu) {
            if (!menu) {
                return ''
            }
            if (menu.id !== undefined && menu.id !== null && menu.id !== '') {
                return this.normalizeKey(menu.id)
            }
            if (menu.meta && menu.meta.id !== undefined && menu.meta.id !== null && menu.meta.id !== '') {
                return this.normalizeKey(menu.meta.id)
            }
            return this.normalizeKey(menu.path)
        },
        getRouteKey(route) {
            if (!route) {
                return ''
            }
            if (route.meta && route.meta.id !== undefined && route.meta.id !== null && route.meta.id !== '') {
                return this.normalizeKey(route.meta.id)
            }
            return this.normalizeKey(route.path)
        },
        // select menu item
        onOpenChange(openKeys) {
            // 在水平模式下时执行，并且不再执行后续
            if (this.mode === 'horizontal') {
                this.openKeys = openKeys
                return
            }
            // 非水平模式时
            const latestOpenKey = openKeys.find(key => !this.openKeys.includes(key))
            if (!this.rootSubmenuKeys.includes(latestOpenKey)) {
                this.openKeys = openKeys
            } else {
                this.openKeys = latestOpenKey ? [latestOpenKey] : []
            }
        },
        updateMenu() {
            const routes = this.$route.matched.concat()
            const { hidden } = this.$route.meta
            let selectedRoute = null
            if (routes.length >= 3 && hidden) {
                routes.pop()
                selectedRoute = routes[routes.length - 1]
            } else {
                selectedRoute = routes.pop()
            }
            this.selectedKeys = [this.getRouteKey(selectedRoute)]
            const openKeys = []
            if (this.mode === 'inline') {
                routes.forEach(item => {
                    openKeys.push(this.getRouteKey(item))
                })
            }
            //update-begin-author:taoyan date:20190510 for:online表单菜单点击展开的一级目录不对
            if (!this.selectedKeys || this.selectedKeys[0].indexOf(":") < 0) {
                this.collapsed ? (this.cachedOpenKeys = openKeys) : (this.openKeys = openKeys)
            }
            //update-end-author:taoyan date:20190510 for:online表单菜单点击展开的一级目录不对
        },
        // render
        renderItem(menu) {
            if (!menu.hidden) {
                return menu.children && !menu.alwaysShow ? this.renderSubMenu(menu) : this.renderMenuItem(menu)
            }
            return null
        },
        renderMenuItem(menu) {
            const target = menu.meta.target || null
            const tag = target && 'a' || 'router-link'
            let props = { to: { name: menu.name } }
            if (menu.route && menu.route === '0') {
                props = { to: { path: menu.path } }
            }

            const attrs = { href: menu.path, target: menu.meta.target }

            if (menu.children && menu.alwaysShow) {
                // 把有子菜单的 并且 父菜单是要隐藏子菜单的
                // 都给子菜单增加一个 hidden 属性
                // 用来给刷新页面时， selectedKeys 做控制用
                menu.children.forEach(item => {
                    //   item.meta = Object.assign(item.meta, { hidden: true })
                })
            }

            return (
                <Item {...{ key: this.getMenuKey(menu) }}>
                    <tag {...{ props, attrs }}>
                        {this.renderIcon(menu.meta.icon)}
                        <span>{menu.meta.title}</span>
                    </tag>
                </Item>
            )
        },
        renderSubMenu(menu) {
            const itemArr = []
            if (!menu.alwaysShow) {
                menu.children.forEach(item => itemArr.push(this.renderItem(item)))
            }
            return (
                <SubMenu {...{ key: this.getMenuKey(menu) }}>
                    <span slot="title">
                        {this.renderIcon(menu.meta.icon)}
                        <span>{menu.meta.title}</span>
                    </span>
                    {itemArr}
                </SubMenu>
            )
        },
        renderIcon(icon) {
            if (icon === 'none' || icon === undefined) {
                return null
            }
            const props = {}
            typeof (icon) === 'object' ? props.component = icon : props.type = icon
            return (
                <Icon {... { props }} />
            )
        }
    },

    render() {
        const { mode, theme, menu } = this
        const props = {
            mode: mode,
            theme: theme,
            openKeys: this.openKeys
        }
        const on = {
            select: obj => {
                this.selectedKeys = obj.selectedKeys
                this.$emit('select', obj)
            },
            openChange: this.onOpenChange
        }

        const menuTree = menu.map(item => {
            if (item.hidden) {
                return null
            }
            return this.renderItem(item)
        })
        // {...{ props, on: on }}
        return (
            <Menu id="leftMenu" vModel={this.selectedKeys} {...{ props, on: on }}>
                {menuTree}
            </Menu>
        )
    }
}
