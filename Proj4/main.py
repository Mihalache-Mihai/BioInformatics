from Bio import Phylo as Parser
import sys


def minFromIntervalList(list):
    min=-1
    for l in list:
        if l[0]<min or min<0:
            min=l[0]
    return min

def maxFromIntervalList(list):
    max=-1
    for l in list:
        if l[1]>max:
            max=l[1]
    return max

def sumFromIntervalList(list):
    sum=0
    for l in list:
        sum+=l[2]
    return sum
class Node:
    def __init__(self,p,name):
        self.parent=p
        self.children=[]
        self.name=name

    def addChildren(self,c):
        for child in c:
            nn=Node(self,child.name)
            self.children.append(nn)
            nn.addChildren(child)

    def isLeaf(self):
        return len(self.children)==0

    def find(self,name):
        if(self.name==name):
            return self
        for c in self.children:
            found=c.find(name)
            if found is not None:
                return found
        return None

    def print(self,level=0):
        for i in range(level):
            sys.stdout.write(' ')
        print(self.name)
        for c in self.children:
            c.print(level+1)
            
    def getAllLeafs(self):
        if self.isLeaf():
            return [self]
        l=[]
        for c in self.children:
            l+=c.getAllLeafs()
        return l

    def step4(self):
        if self.isLeaf():
            return (self.name,self.name,1)
        intervals=[]
        for c in self.children:
            intervals+=[c.step4()]
        min=minFromIntervalList(intervals)
        max=maxFromIntervalList(intervals)
        sum=sumFromIntervalList(intervals)
        self.name=(min,max,sum)
        return (min,max,sum)

    def getIntervalsOfChildNonTerminals(self):
        intervals=[]
        for c in self.children:
            if(not c.isLeaf()):
                intervals.append(c.name)
                for i in c.getIntervalsOfChildNonTerminals():
                    intervals.append(i)
        return intervals



def toTop(child):
    oldparent=child.parent
    oldparent.children.remove(child)
    child.children.append(oldparent)
    newParent=oldparent.parent
    oldparent.parent=child
    child.parent=newParent
    if(newParent is not None):
        newParent.children.append(child)
        newParent.children.remove(oldparent)


if len(sys.argv)<2:
    file1=input("first tree (file in newick format)")
else:
    file1=sys.argv[1]

if len(sys.argv)<3:
    file2=input("second tree (file in newick format)")
else:
    file2=sys.argv[2]


tree1=Parser.read(file1,'newick')
tree2=Parser.read(file2,'newick')

root1=Node(None,tree1.clade.name)
root1.addChildren(tree1.clade)

leaf=root1
while not leaf.isLeaf():
    leaf=leaf.children[0]

while(leaf.parent is not None):
    toTop(leaf)


root1=leaf

root2=Node(None,tree2.clade.name)
root2.addChildren(tree2.clade)
leaf=root2.find(root1.name)

while(leaf.parent is not None):
    toTop(leaf)

root2=leaf
#now step 1 is done and both are rooted with the same leaf

origNames={}
newNames={}
cur=1

leafs1=root1.getAllLeafs()
for l in leafs1:
    origNames[cur]=l.name
    newNames[l.name]=cur
    l.name=cur
    cur+=1

leafs2=root2.getAllLeafs()
for l in leafs2:
    l.name=newNames[l.name]

#now step 4 follows

root1.step4()
intervals1=root1.getIntervalsOfChildNonTerminals()

def eliminateWrong(intervals):
    for i in intervals:
        if i[1]-i[0] +1!= i[2]:
            intervals.remove(i)
    return intervals
    

root2.step4()
intervals2=root2.getIntervalsOfChildNonTerminals()
intervals2=eliminateWrong(intervals2)

def eliminateDuplets(intervals):
    nIntervals=[intervals[0]]
    for i in range(1,len(intervals)):
        if(intervals[i]!=intervals[i-1]):
            nIntervals.append(intervals[i])
    return nIntervals

intervals1.sort()
intervals2.sort()
intervals1=eliminateDuplets(intervals1)
intervals2=eliminateDuplets(intervals2)

i=0
j=0
count=0
while i<len(intervals1) and j<len(intervals2):
    if(intervals1[i]==intervals2[j]):
        i+=1
        j+=1
        count+=1
    elif intervals1[i]>intervals2[j]:
        j+=1
    else:
        i+=1

len1=len(intervals1)

print(len1,"different intervals in each, thus",2*len1,"in total")
print("Shared are ",count,";so we have a RF-Score of",2*len1-2*count)