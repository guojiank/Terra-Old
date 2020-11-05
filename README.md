# Terra

Terra is a top down, 2D, world generator aimed at generating, populating, and exporting realistic worlds based on a multitude of factors.

### Advanced Perlin Noise
Terra's perlin noise allows for a multitude of powerful functions. These range from optional fall off maps to controllable octaves, persistance, lacunarity, and pseudorandom seeding.


|               *Base Noise*               |             *Fall Off Maps*              |          *Controllable Values*           |                 *Seeds*                  |
| :--------------------------------------: | :--------------------------------------: | :--------------------------------------: | :--------------------------------------: |
| ![Noise Map](http://i.imgur.com/eGgUwjO.png "Perlin noise input") | ![Fall Off Map](http://i.imgur.com/dofrsw8.png "Fall off map") | ![Controlled Map](http://i.imgur.com/pp1DD60.png "Controlled noise") | ![Seeded Map](http://i.imgur.com/GrxPJ8T.png "Different seed, same noise") |



So far, this has been utilized to create perlin noise based height maps!



|               *Raw Noise*                |           *Example Height Map*           |
| :--------------------------------------: | :--------------------------------------: |
| ![Noise Map](http://i.imgur.com/h02hAMN.png "Perlin noise input") | ![Height Map](http://i.imgur.com/OpAxzvo.png "Example height map") |

|                  *GUI*                   |
| :--------------------------------------: |
| ![GUI](http://i.imgur.com/5Ck4t6P.png "User friendly GUI") |

## 原理和步骤
1. 生成perlin噪声二维数组，并获取数组中的最大值和最小值，然后根据最大和最小值将二维数组的值规范到0值1之间
2. 设计一个强度值，取值范围在1%~60%,强度值会影响一个点的权重值，越靠近边缘点的权值越低。目的集中噪声点在地图中央。
3. 噪声值根据权值集中后，再规范到0-1之间
4. 生成白点噪声数组
5. 白点噪声数组每个元素乘以一个系数（可正可负）后，和perlin噪声数组相加，并记录最大值和最小值
6. 规范到0-1之间
7. 转换为高度图显示出来