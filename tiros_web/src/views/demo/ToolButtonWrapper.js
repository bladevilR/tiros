import { getSlot } from '@comp/_util/util'

const FLOAT_PRECISION_ADJUST = 0.5;

export default {
  name: 'ToolButtonWrapper',
  props: {
    space: {
      type: Boolean,
      default: false
    }
  },
  data() {
    this.resizeObserver = null;
    this.mutationObserver = null;

    return {
      lastVisibleIndex: undefined,
    };
  },
  mounted () {
    this.$nextTick(() => {
      this.setChildrenWidthAndResize()
    })
  },
  methods: {
    findDOMNode (instance) {
      let node = instance && (instance.$el || instance);
      while (node && !node.tagName) {
        node = node.nextSibling;
      }
      return node;
    },
    getWidth (elem) {
      let width =
        elem && typeof elem.getBoundingClientRect === 'function' && elem.getBoundingClientRect().width;
      if (width) {
        width = +width.toFixed(6);
      }
      return width || 0;
    },
    setChildrenWidthAndResize () {
      const _this = this.findDOMNode(this)
      // console.log('this:', _this.getBoundingClientRect().right)
      const maxWidth = _this.getBoundingClientRect().right -100

      const itemNodes = this.getItemNodes();
      let curWidth=0;
      itemNodes.map((item,index) => {
        curWidth += this.getWidth(item) + 8
        console.log('maxWidth:%d, curWidth:%d', maxWidth, curWidth)

        if(curWidth > maxWidth && this.lastVisibleIndex === undefined)
        {
          this.lastVisibleIndex = index
        }
      })
      if (this.lastVisibleIndex && this.lastVisibleIndex >1) {
        this.lastVisibleIndex = this.lastVisibleIndex - 1
        this.$forceUpdate()
      }
    },

    getItemNodes () {
      const ul = this.findDOMNode(this);
      if (!ul) {
        return [];
      }
      let childrenNodes = ul.children;
      if (this.space) {
        childrenNodes = childrenNodes[0].children
      }
      console.log('children:', childrenNodes)

      const rets = []
      for (let i = 0; i < childrenNodes.length; i++) {
        rets.push(childrenNodes[i]);
      }

      return rets
    },
    renderChildren (children) {
      if (this.lastVisibleIndex) {
        const ret = []
        const hides = []
        children.forEach((item, index) => {
          if (index <= this.lastVisibleIndex) {
            ret.push(item)
          } else {
            hides.push(item)
          }
        })

        if(hides.length>0){
          const items = []
          hides.forEach(item => {
            item.componentOptions.propsData.type='link'
            items.push(<a-menu-item >{item}</a-menu-item>)
          })
          let more=<a-dropdown>
            <a-menu slot="overlay" >
              {items}
          </a-menu>
          <a-button> 更多<a-icon type="down" /> </a-button>
        </a-dropdown>
          ret.push(more)
        }
        //
        return ret
      } else {
        return children
      }
    }
  },
  render() {
    if (this.space) {
      return <div style="width:520px; overflow:hidden;white-space: nowrap;">
        <a-space>{this.renderChildren(getSlot(this))}</a-space>
      </div>
    } else {
      return <div style="width:100%; overflow:hidden;white-space: nowrap;">
        {this.renderChildren(getSlot(this))}
      </div>
    }
  }
}