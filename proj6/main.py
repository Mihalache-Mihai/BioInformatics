import sys

def followed(list,start,step):
    val=list[start]
    res=0
    while val==list[start]:
        res+=1
        start+=step
    return res

def toNextM(l,start):
    res=0
    while l[start]!='m':
        start+=1
        res+=1
        if(start==len(l)):
            return -1
    return res

if len(sys.argv)<2:
    print("Missing argument: hp-string")
    sys.exit(1)

hpList=list(sys.argv[1])
#what is even, what is odd
eoList=[]
for i in range(len(hpList)):
   if hpList[i]=='h':
       eoList.append(i%2)

#step1
leftmost=-1
rightmost=-1
left=0
right=len(eoList)-1
while(left<right):
    if(left==right):
        break;
    if eoList[left]!=eoList[right]: #match
        eoList[left]=2
        eoList[right]=2
        leftmost=left
        rightmost=right
        left+=1
        right-=1
    elif followed(eoList,left,1)>followed(eoList,right,-1):
        eoList[right]=-1
        right-=1
    else:
        eoList[left]=-1
        left+=1


#getFullListBack
fList=[]
eoInd=0
for i in range(len(hpList)):
    if hpList[i]=='h':
        if eoList[eoInd]==2:
            fList.append('m')#matchet
        else:
            fList.append('u')#unmatched
        eoInd+=1
    else:
        fList.append('p')

def countX(l,v):
    if v<0:
        return v
    res=0
    while v>=0:
        if l[res]!='p':
            v-=1
        res+=1
    return res-1
print(leftmost,rightmost)
leftmost=countX(fList,leftmost)
rightmost=countX(fList,rightmost)
#step2
i=0
print(fList,leftmost,rightmost)
if(leftmost<0):
    for i in range(len(fList))-1:
        print("e",end='')
    print()
    sys.exit(0)
while(fList[i]!='m'):
    print("e",end='')
    i+=1
    #print("\n",i)

#print("\n",i)
while i<leftmost:
    dist=toNextM(fList,i+1)
 #   print(i,dist)
    stepsUp=(dist-(dist%2))//2
    for j in range(stepsUp):
        print("n",end='')
    print("e",end='')
    for j in range(stepsUp):
        print("s",end='')
    if dist%2==1:
        print("e",end='')
    i+=(dist+1)
  #  print("\nkk",i)
dist=rightmost-leftmost
stepsUp=(dist-(dist%2))//2
for j in range(stepsUp):
    print("e",end='')
print("s",end='')
for j in range(stepsUp):
    print("w",end='')
i=rightmost
#print("\n",i)

while i<len(fList)-1:
    dist=toNextM(fList,i+1)
   # print("\nui",i,dist)
    if(dist==-1):
        while(i<len(fList)-1):
            print("w",end='')
            i+=1
        break;

    stepsDown=(dist-(dist%2))//2
    for j in range(stepsDown):
        print("s",end='')
    print("w",end='')
    for j in range(stepsDown):
        print("n",end='')
    if dist%2==1:
        print("w",end='')
    i+=dist+1
print()