import sys

class Node:
    def __init__(self,name):
        self.name=name
        self.children=[]

    def addChild(self,child,distance):
        self.children.append((child,distance))
        child.children.append((self,distance))

    def setThreeEdges(self,e1,e2,e3):
        self.children[0]=(self.children[0][0],e1)
        self.children[1]=(self.children[1][0],e2)
        self.children[2]=(self.children[2][0],e3)
        self.children[0][0].children[0]=(self,e1)
        self.children[1][0].children[0]=(self,e2)
        self.children[2][0].children[0]=(self,e3)


    def removeChild(self,child):
        self.children.remove(child)
        child[0].children.remove((self,child[1]))

    def getNewick(self,parent):
        if parent==None or len(self.children)>1:
            res='('
            for (c,d) in self.children:
                if c!=parent:
                    if res!='(':
                        res+=','
                    res+=c.getNewick(self)
                    res+=':'+str(round(d,3))
            res+=')'
            res+=self.name
            return res
        else:
            return self.name

if len(sys.argv)<2:
    file=input("File with distance matrix:")
else:
    file=sys.argv[1]

file=open(file)
nodeNumber=int(file.readline()[:-1])
root=Node('')
matrix=[]
for i in range(nodeNumber):
    splits=file.readline()[:-1].split()
    nNode=Node(splits[0])
    root.addChild(nNode,-1)
    row=[]
    for i in range(nodeNumber):
        row.append(float(splits[1+i]))
    matrix.append(row)

file.close()


S=root.children
rL=[]
def r(i):
    sum=0.0
    for j in range(len(matrix)):
        sum+=matrix[i][j]
    return sum/(len(matrix)-2)

for i in range(len(S)):
    rL.append(r(i))


while len(S)>3:
    min=None
    (i,j)=(0,0)
    for m in range(len(S)):
        for n in range(m+1,len(S)):
            v=matrix[m][n]-rL[m]-rL[n]
            if min==None or v<min:
                min=v
                (i,j)=(m,n)
    k=Node('')
    root.addChild(k,-1)
    kiedge=0.5*(matrix[i][j]+rL[i]-rL[j])
    k.addChild(S[i][0],kiedge)
    k.addChild(S[j][0],matrix[i][j]-kiedge)
    root.removeChild(S[j])
    root.removeChild(S[i])
    row=[]
    for l in range(len(matrix)):
        if l!=i and l!=j:
            row.append(0.5*(matrix[i][l]+matrix[j][l]-matrix[i][j]))
    row.append(0.0)#self
    matrix.append(row)
    matrix.remove(matrix[j])
    matrix.remove(matrix[i])
    rL.pop(j)
    rL.pop(i)
    for l in range(len(matrix)-1):
        rL[l]=(rL[l]*(len(S)-1)+row[l]-matrix[l][i]-matrix[l][j])/(len(S)-2)
        matrix[l].pop(j)
        matrix[l].pop(i)
        matrix[l].append(row[l])
    rL.append(r(len(matrix)-1))


dij=matrix[0][1]*0.5
dim=matrix[0][2]*0.5
djm=matrix[1][2]*0.5
root.setThreeEdges(dij+dim-djm,dij+djm-dim,dim+djm-dij)

newick=root.children[0][0].getNewick(None)
print(newick)
file=open('output.newick','w')
file.write(newick)
file.close()