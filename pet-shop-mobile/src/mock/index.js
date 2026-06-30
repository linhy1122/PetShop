/**
 * Mock 静态数据 —— 无需后端即可展示所有页面 UI
 * 每个 API 接口都有对应的 mock 数据，页面在 API 失败时自动降级使用
 */

// ==================== 首页 ====================

export const carouselImages = [
  'https://picsum.photos/seed/pet1/750/400',
  'https://picsum.photos/seed/pet2/750/400',
  'https://picsum.photos/seed/pet3/750/400'
]

export const petCategories = [
  { id: 1, icon: '🐕', name: '狗狗' },
  { id: 2, icon: '🐱', name: '猫咪' },
  { id: 3, icon: '🐹', name: '小宠' },
  { id: 4, icon: '🐟', name: '水族' },
  { id: 5, icon: '🐦', name: '鸟类' },
  { id: 6, icon: '🦴', name: '狗粮' },
  { id: 7, icon: '🐟', name: '猫粮' },
  { id: 8, icon: '🧸', name: '玩具' }
]

export const hotProducts = [
  { id: 1, name: '布偶猫 纯种幼猫', price: 2800, sales: 128, mainImage: 'https://picsum.photos/seed/cat1/400/300', description: '蓝双色布偶幼猫，温顺粘人', productType: 1, breed: '布偶猫', age: '3个月', gender: 2 },
  { id: 2, name: '泰迪贵宾犬 小体', price: 1800, sales: 256, mainImage: 'https://picsum.photos/seed/dog1/400/300', description: '茶杯泰迪，聪明活泼', productType: 1, breed: '泰迪', age: '2个月', gender: 1 },
  { id: 3, name: '英短蓝猫 幼猫', price: 2200, sales: 89, mainImage: 'https://picsum.photos/seed/cat2/400/300', description: '纯种英短蓝猫', productType: 1, breed: '英短', age: '4个月', gender: 1 },
  { id: 4, name: '天然猫粮 5kg', price: 168, sales: 1520, mainImage: 'https://picsum.photos/seed/food1/400/300', description: '高蛋白无谷天然粮', productType: 2, stock: 500 },
  { id: 5, name: '宠物自动喂食器', price: 299, sales: 680, mainImage: 'https://picsum.photos/seed/feeder/400/300', description: '智能定时投喂', productType: 2, stock: 200 },
  { id: 6, name: '金毛寻回犬 幼犬', price: 1500, sales: 312, mainImage: 'https://picsum.photos/seed/dog2/400/300', description: '纯种金毛幼犬，温顺友善', productType: 1, breed: '金毛', age: '2个月', gender: 1 },
  { id: 7, name: '猫爬架 豪华款', price: 458, sales: 920, mainImage: 'https://picsum.photos/seed/tree/400/300', description: '多层猫爬架带猫窝', productType: 2, stock: 100 },
  { id: 8, name: '仓鼠笼 双层别墅', price: 128, sales: 450, mainImage: 'https://picsum.photos/seed/cage/400/300', description: '超大空间双层设计', productType: 2, stock: 300 }
]

// ==================== 商品列表 ====================

export const categories = [
  { id: 1, name: '狗狗', type: 1 }, { id: 2, name: '猫咪', type: 1 },
  { id: 3, name: '小宠', type: 1 }, { id: 4, name: '水族', type: 1 },
  { id: 5, name: '鸟类', type: 1 }, { id: 6, name: '狗粮', type: 2 },
  { id: 7, name: '猫粮', type: 2 }, { id: 8, name: '零食', type: 2 },
  { id: 9, name: '玩具', type: 2 }, { id: 10, name: '日用品', type: 2 },
  { id: 11, name: '医疗保健', type: 2 }, { id: 12, name: '服饰', type: 2 }
]

export const allProducts = [
  ...hotProducts,
  { id: 9, name: '边境牧羊犬 幼犬', price: 2500, sales: 76, mainImage: 'https://picsum.photos/seed/dog3/400/300', description: '智商第一的牧羊犬', productType: 1, breed: '边牧', age: '3个月', gender: 2 },
  { id: 10, name: '暹罗猫 幼猫', price: 1600, sales: 134, mainImage: 'https://picsum.photos/seed/cat3/400/300', description: '重点色暹罗猫', productType: 1, breed: '暹罗', age: '3个月', gender: 1 },
  { id: 11, name: '仓鼠 金丝熊', price: 35, sales: 890, mainImage: 'https://picsum.photos/seed/hamster/400/300', description: '活泼可爱的金丝熊', productType: 1, breed: '金丝熊', age: '1个月', gender: 0 },
  { id: 12, name: '狗窝 可拆洗', price: 89, sales: 2100, mainImage: 'https://picsum.photos/seed/doghouse/400/300', description: '柔软舒适可拆洗', productType: 2, stock: 600 },
  { id: 13, name: '宠物梳毛器', price: 39, sales: 3200, mainImage: 'https://picsum.photos/seed/brush/400/300', description: '一键除毛不伤肤', productType: 2, stock: 800 },
  { id: 14, name: '哈士奇 幼犬', price: 1200, sales: 203, mainImage: 'https://picsum.photos/seed/dog4/400/300', description: '蓝眼三把火哈士奇', productType: 1, breed: '哈士奇', age: '2个月', gender: 1 },
  { id: 15, name: '猫砂 豆腐砂 6L', price: 29, sales: 5600, mainImage: 'https://picsum.photos/seed/litter/400/300', description: '除臭结团可冲厕', productType: 2, stock: 2000 },
  { id: 16, name: '宠物饮水机', price: 199, sales: 1340, mainImage: 'https://picsum.photos/seed/water/400/300', description: '循环过滤活水', productType: 2, stock: 350 }
]

// ==================== 店铺 ====================

export const stores = [
  { id: 1, name: '萌宠之家', image: 'https://picsum.photos/seed/store1/400/300', rating: 4.8, province: '福建省', city: '厦门市', district: '思明区', address: '湖滨南路 88 号', phone: '0592-1234567', businessHours: '09:00-21:00', longitude: 118.089, latitude: 24.479, description: '十年老店，专注宠物健康' },
  { id: 2, name: '爱心宠物医院', image: 'https://picsum.photos/seed/store2/400/300', rating: 4.6, province: '福建省', city: '厦门市', district: '湖里区', address: '嘉禾路 168 号', phone: '0592-2345678', businessHours: '08:00-20:00', longitude: 118.102, latitude: 24.508, description: '专业宠物医疗团队' },
  { id: 3, name: '汪汪宠物生活馆', image: 'https://picsum.photos/seed/store3/400/300', rating: 4.9, province: '福建省', city: '厦门市', district: '集美区', address: '石鼓路 56 号', phone: '0592-3456789', businessHours: '10:00-22:00', longitude: 118.065, latitude: 24.582, description: '宠物美容洗澡一站式' },
  { id: 4, name: '猫咪主题咖啡馆', image: 'https://picsum.photos/seed/store4/400/300', rating: 4.7, province: '福建省', city: '厦门市', district: '思明区', address: '曾厝垵 22 号', phone: '0592-4567890', businessHours: '10:00-22:00', longitude: 118.112, latitude: 24.436, description: '撸猫+咖啡，治愈系空间' }
]

// ==================== 购物车 ====================

export const cartItems = [
  { id: 1, productId: 4, productName: '天然猫粮 5kg', productImage: 'https://picsum.photos/seed/food1/200/200', price: 168, quantity: 2, checked: 1 },
  { id: 2, productId: 5, productName: '宠物自动喂食器', productImage: 'https://picsum.photos/seed/feeder/200/200', price: 299, quantity: 1, checked: 1 },
  { id: 3, productId: 12, productName: '狗窝 可拆洗', productImage: 'https://picsum.photos/seed/doghouse/200/200', price: 89, quantity: 1, checked: 0 }
]

// ==================== 订单 ====================

export const orders = [
  { id: 1, orderNo: 'PS20240615001', status: 4, totalAmount: 636, payAmount: 636, payMethod: '支付宝', createTime: '2024-06-15 14:30:00', logisticsCompany: '顺丰快递', logisticsNo: 'SF1234567890' },
  { id: 2, orderNo: 'PS20240620002', status: 2, totalAmount: 1800, payAmount: 1800, payMethod: '微信支付', createTime: '2024-06-20 09:15:00', logisticsCompany: '中通快递', logisticsNo: 'ZT9876543210' },
  { id: 3, orderNo: 'PS20240623003', status: 0, totalAmount: 458, payAmount: 458, payMethod: null, createTime: '2024-06-23 20:45:00' },
  { id: 4, orderNo: 'PS20240624004', status: 1, totalAmount: 299, payAmount: 299, payMethod: '支付宝', createTime: '2024-06-24 11:20:00' },
  { id: 5, orderNo: 'PS20240625005', status: 3, totalAmount: 2800, payAmount: 2800, payMethod: '微信支付', createTime: '2024-06-25 16:00:00' }
]

// ==================== 地址 ====================

export const addresses = [
  { id: 1, receiverName: '张三', receiverPhone: '13800138000', province: '福建省', city: '厦门市', district: '思明区', detail: '湖滨南路 88 号 3 楼', isDefault: 1 },
  { id: 2, receiverName: '张三', receiverPhone: '13800138000', province: '福建省', city: '福州市', district: '鼓楼区', detail: '五四路 168 号', isDefault: 0 }
]

// ==================== 视频 ====================

export const videos = [
  { id: 1, title: '布偶猫日常撒娇合集', cover: 'https://picsum.photos/seed/video1/400/300', duration: 185, viewCount: 12500, likeCount: 890, videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4', description: '超可爱的布偶猫日常' },
  { id: 2, title: '金毛犬训练教程', cover: 'https://picsum.photos/seed/video2/400/300', duration: 420, viewCount: 8900, likeCount: 650, videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4', description: '从零开始训练你的金毛' },
  { id: 3, title: '猫咪洗澡的正确姿势', cover: 'https://picsum.photos/seed/video3/400/300', duration: 258, viewCount: 21000, likeCount: 1500, videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4', description: '教你如何给猫咪洗澡' },
  { id: 4, title: '狗狗玩具开箱测评', cover: 'https://picsum.photos/seed/video4/400/300', duration: 315, viewCount: 6700, likeCount: 420, videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4', description: '最新狗狗玩具开箱' },
  { id: 5, title: '英短蓝猫成长记录', cover: 'https://picsum.photos/seed/video5/400/300', duration: 198, viewCount: 9800, likeCount: 730, videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4', description: '记录小蓝猫从2个月到1岁' },
  { id: 6, title: '宠物梳毛技巧分享', cover: 'https://picsum.photos/seed/video6/400/300', duration: 142, viewCount: 4500, likeCount: 310, videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4', description: '换毛季必备技巧' }
]

// ==================== 商品评价 ====================

export const reviews = [
  { id: 1, rating: 5, content: '非常可爱的猫咪，性格温顺，已经会自己用猫砂盆了！', createTime: '2024-06-10' },
  { id: 2, rating: 4, content: '狗狗很健康活泼，就是有点调皮，需要耐心训练。', createTime: '2024-06-08' },
  { id: 3, rating: 5, content: '猫粮质量很好，猫咪很爱吃，便便也正常。', createTime: '2024-06-05' },
  { id: 4, rating: 4, content: '喂食器使用方便，定时功能很准，出差再也不用担心了。', createTime: '2024-06-01' }
]

// ==================== 管理后台统计 ====================

export const adminStats = { products: 128, stores: 15, orders: 356, users: 1024 }

// ==================== 用户信息 ====================

export const mockUser = {
  userId: 1,
  username: 'demo_user',
  nickname: '宠物达人',
  phone: '138****8000',
  email: 'pet@example.com',
  role: 'user'
}
