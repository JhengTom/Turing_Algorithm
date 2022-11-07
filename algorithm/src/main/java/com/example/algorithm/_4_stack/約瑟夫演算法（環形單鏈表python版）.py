 class Node(object):
    def __init__(self, item):
        self.elem = item
        self.next = None


class RoundList(object):
    def __init__(self, node=None):
        if node:
            node.next = node
        self.__head = node

    def is_empty(self):
        return self.__head == None

    def lager(self):
        count = 1
        cur = self.__head
        if self.is_empty():
            return 0
        else:
            while cur.next != self.__head:
                cur = cur.next
                count += 1
        return count

    def travel(self):
        if self.is_empty():
            return 0
        cur = self.__head
        while cur.next != self.__head:
            print(cur.elem, end=' ')
            cur = cur.next
        print(cur.elem)

    def add(self, item):
        """头部插入"""
        node = Node(item)
        if self.is_empty():
            self.__head = node
            self.__head.next = self.__head
        else:
            cur = self.__head
            while cur.next != self.__head:
                cur = cur.next

            node.next = self.__head
            self.__head = node
            cur.next = self.__head

    def append(self, item):
        """尾部插入"""
        node = Node(item)
        if self.is_empty():
            self.__head = node
            self.__head.next = self.__head
        else:
            cur = self.__head
            while cur.next != self.__head:
                cur = cur.next

            cur.next = node
            node.next = self.__head

    def insert(self, pos, item):
        count = 0
        if pos <= 0:
            self.add(item)
        elif pos > self.lager() - 1:
            self.append(item)
        else:
            node = Node(item)
            cur = self.__head
            while count < pos - 1:
                count += 1
                cur = cur.next
            node.next = cur.next
            cur.next = node

    def search(self, item):
        if self.is_empty():
            return False
        cur = self.__head
        while cur.next != self.__head:
            if cur.elem == item:
                return True
            else:
                cur = cur.next
        if cur.value == item:
            return True
        return False

    def remove(self, item):
        cur = self.__head
        prior = None
        if self.is_empty():
            return False
        while cur.next != self.__head:
            if cur.elem == item:
                if cur == self.__head:
                    rear = self.__head
                    while rear.next != self.__head:
                        rear = rear.next
                    self.__head = cur.next
                    rear.next = self.__head
                else:
                    # 核心代码  原则是 利用 prior复刻了节点，之后将复刻的节点
                    # （之前都连着） 直接指向下下个节点以达到删除的目的
                    prior.next = cur.next

                return
            else:
                prior = cur
                cur = cur.next
        if cur.elem == item:
            # 如果链表中只有一个元素，则此时prior为None，Next属性就会报错
            # 此时直接使其头部元素为None即可
            if cur == self.__head:
                self.__head = None
                return
            prior.next = cur.next

    def header(self):
        return self.__head


def main(num, rules):
    rl = RoundList()
    # 制作环形链表
    for i in range(1, num+1):
        rl.append(i)
    # 开始做筛除算法
    p = rl.header()
    j = 0
    while True:
        j += 1
        if j == rules:
            rl.remove(p.elem)
            j = 0
            rl.travel()
        if rl.lager() == 1:
            rl.travel()
            break
        p = p.next


if __name__ == '__main__':
    main(10, 3)


