import request from '@/utils/request'

/** 获取分类列表（可按类型筛选：1=宠物, 2=宠物周边） */
export function getCategoryListApi(type) {
  return request.get('/category/list', { params: { ...(type ? { type } : {}) } })
}
