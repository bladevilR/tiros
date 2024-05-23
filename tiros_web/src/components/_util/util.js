/**
 * components util
 */

/**
 * 清理空值，对象
 * @param children
 * @returns {*[]}
 */
export function filterEmpty (children = []) {
  return children.filter(c => c.tag || (c.text && c.text.trim() !== ''))
}

const flattenChildren = (children = [], filterEmpty = true) => {
  const temp = Array.isArray(children) ? children : [children];
  const res = [];
  temp.forEach(child => {
    res.push(child);
  });
  return res;
};

const getSlot = (self, name = 'default', options = {}) => {

  let res = self._self.$slots[name];
  return flattenChildren(res);
};

export {getSlot}