import { getSlot } from '@comp/_util/util'
import ResizeObserver from 'resize-observer-polyfill'

const FLOAT_PRECISION_ADJUST = 0.5;

export default {
  name: 'na-buttons',
  props: {
    space: {
      type: Boolean,
      default: true
    }
  },
  data() {
    this.resizeObserver = null;
    this.mutationObserver = null;
    return {
      lastVisibleIndex: undefined
    };
  },
  mounted () {
    this.$nextTick(() => {
      this.setChildrenWidthAndResize()
      const buttonWrapper = this.findDOMNode(this);

      // 监控尺寸变化
      this.resizeObserver = new ResizeObserver(entries => {
        entries.forEach(this.setChildrenWidthAndResize);
      });
      this.resizeObserver.observe(buttonWrapper);

      // 监控元素的增加、删除
      if (typeof MutationObserver !== 'undefined') {
        this.mutationObserver = new MutationObserver(() => {
          this.resizeObserver.disconnect();
          this.resizeObserver.observe(buttonWrapper);
          this.setChildrenWidthAndResize();
        });
        this.mutationObserver.observe(buttonWrapper, {
          attributes: false,
          childList: true,
          subTree: false,
        });
      }
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
      let width = elem && typeof elem.getBoundingClientRect === 'function' && elem.getBoundingClientRect().width;
      if (width) {
        width = +width.toFixed(6);
      }
      return width || 0;
    },
    setChildrenWidthAndResize () {
      const _this = this.findDOMNode(this)

      let maxWidth = _this.getBoundingClientRect().right
      // console.log('max width:', _this.getBoundingClientRect().width)
      const itemNodes = this.getItemNodes();
      if (this.space) {
        maxWidth = maxWidth - 83 // 这里不知道为什么多了83
      }

      let curWidth = _this.getBoundingClientRect().left
      for (let i = 0; i < itemNodes.length; i++) {
        const item = itemNodes[i]
        curWidth += this.getWidth(item)
        if (this.space) {
          curWidth += 8
        }
        // console.log('maxWidth:%d, curWidth:%d', maxWidth, curWidth)

        if (curWidth > maxWidth) {
          this.lastVisibleIndex = i
          if (curWidth + 90 > maxWidth) {
            this.lastVisibleIndex--
          }
          break
        }
      }
      // console.log('lastVisibleIndex:', this.lastVisibleIndex)

      this.$forceUpdate()
    },

    getItemNodes () {
      const ul = this.findDOMNode(this);
      if (!ul) {
        return [];
      }
      let childrenNodes = ul.children;

      const rets = []
      for (let i = 0; i < childrenNodes.length; i++) {
        rets.push(childrenNodes[i]);
      }

      return rets
    },
    renderChildren (children) {
      let lastIndex= children.length
      if (this.lastVisibleIndex !== undefined) {
        lastIndex=this.lastVisibleIndex
      }
      const ret = []
      const hides = []
      children.forEach((item, index) => {
        if (index <= lastIndex) {
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
      if (this.space) {
        ret.forEach(item => {
          if(item.elm) {
            item.elm.style = 'margin-right:8px;'
          }
        })
      }
      return ret
    }
  },
  render() {
    return <div style="width:100%; overflow:hidden;white-space: nowrap;">
      {this.renderChildren(getSlot(this))}
    </div>
  }
}