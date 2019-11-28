import sys

def followed(list,start,step):
    val=list[start]
    res=0
    while val==list[start]:
        res+=1
        start+=step
    return res

def toNextM(l,start,dir):
    res=0
    while l[start]!='m':
        start+=dir
        res+=1
        if(start==len(l) or start < 0):
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
            fList.append('m')#matched
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

#the loop at the right side
middle=[]
dist=rightmost-leftmost
stepsUp=(dist-(dist%2))//2
for j in range(stepsUp):
    middle.append('e')
middle.append('s')
for j in range(stepsUp):
    middle.append('w')


above=[]
below=[]
aflag=True
bflag=True
feelFree=None
while True:
    print(leftmost,rightmost,"dd")
    if leftmost == 0 or rightmost == len(fList)-1:
        while(leftmost>0):
            above.append('e')
            leftmost-=1
        while(rightmost<len(fList)-1):
            below.append('w')
            rightmost+=1
        break;
    dabove=toNextM(fList,leftmost-1,-1)
    dbelow=toNextM(fList,rightmost+1,1)
    if dabove < 0:
        assert dbelow < 0
        while(leftmost>0):
            above.append('e')
            leftmost-=1
        while(rightmost<len(fList)-1):
            below.append('w')
            rightmost+=1
        break;
    assert dbelow >=0
    assert dabove%2==dbelow%2
    print(dbelow,dabove,"ee")
    if dabove%2==0:
        feelFree = aflag and bflag
        if dabove==0:
            above.append('e')
            aflag=True
        else:
            if not feelFree:
                above.append('e')
                dabove-=2
            for i in range((dabove)//2):
                above.append('s')
            above.append('e')
            for i in range((dabove)//2):
                above.append('n')
            if not feelFree:
                above.append('e')
                dabove+=2
            aflag=not feelFree
        if dbelow==0:
            below.append('w')
            bflag=True
        else:
            if not feelFree:
                below.append('w')
                dbelow-=2
            for i in range((dbelow)//2):
                below.append('s')
            below.append('w')
            for i in range((dbelow)//2):
                below.append('n')
            if not feelFree:
                below.append('w')
                dbelow+=2
            bflag=not feelFree

    else:   
        if not aflag:
            above.append('e')
        for i in range((dabove-1)//2):
            above.append('s')
        above.append('e')
        for i in range((dabove-1)//2):
            above.append('n')
        if aflag:
            above.append('e')
        if not bflag:
            below.append('w')
        for i in range((dbelow-1)//2):
            below.append('s')
        below.append('w')
        for i in range((dbelow-1)//2):
            below.append('n')
        if bflag:
            below.append('w')
        #flags remain
    
    leftmost-=(dabove+1)
    rightmost+=(dbelow+1)

above.reverse()
print(''.join(above+middle+below))     
'''
x=0
stack=[]
#head
while(fList[i]!='m'):
    print("e",end='')
    i+=1
    x+=1
    #print("\n",i)
stack.append(x)
flag=True
#first way
while i<leftmost:
    dist=toNextM(fList,i+1)
    if(dist%2==0):
        if flag:
            for u in range(dist//2):
                print("n",end='')
            print("e",end='')
            x+=1
            flag=True
            for u in range(dist//2):
                print("s",end='')
                flag=False
            stack.append(x)
            i+=(dist+1)
        else:
            if dist > 0:
                print("e",end='')
                x+=1
            for u in range((dist-2)//2):
                print("n",end='')
            print("e",end='')
            x+=1
            for u in range((dist-2)//2):
                print("s",end='')
            if dist > 0:
                print("e",end='')
                x+=1
            stack.append(x)
            flag=True
            i+=(dist+1)
    else:
        if flag:
            for u in range((dist-1)//2):
                print("n",end='')
            print("e",end='')
            x+=1
            for u in range((dist-1)//2):
                print("s",end='') 
            print("e",end='')
            x+=1
            stack.append(x)
            flag=True
            i+=(dist+1)
        else:
            print("e",end='')
            x+=1
            for u in range((dist-1)//2):
                print("n",end='')
            print("e",end='')
            x+=1
            flag=True
            for u in range((dist-1)//2):
                print("s",end='') 
                flag=False
            stack.append(x)
            i+=(dist+1)


#print("\n",i)
while i<leftmost:
    dist=toNextM(fList,i+1)
 #   print(i,dist)
    stepsUp=(dist-(dist%2))//2
    if dist%2==1:
        print("e",end='')
    for j in range(stepsUp):
        print("n",end='')
    print("e",end='')
    for j in range(stepsUp):
        print("s",end='')
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
x=stack.pop()
while len(stack)>0:
    nx=stack.pop()
    dist=x-nx
    total=toNextM(fList,i+1)+1-dist
    if(dist>1):
        print("w",end='')
        dist-=1
    for u in range(total//2):
        print("s",end='')
    print("w",end='')
    dist-=1
    for u in range(total//2):
        print("n",end='')
    for u in range(dist):
        print("w",end='')
    i+=(toNextM(fList,i+1)+1)
    x=nx

    if(dist%2==0):
        if flag:
            for u in range(dist//2):
                print("s",end='')
            print("w",end='')
            flag=True
            for u in range(dist//2):
                print("n",end='')
                flag=False
            i+=(dist+1)
        else:
            print("w",end='')
            for u in range((dist-2)//2):
                print("s",end='')
            print("w",end='')
            for u in range((dist-2)//2):
                print("n",end='')
            print("w",end='')
            flag=True
            i+=(dist+1)
    else:
        if flag:
            for u in range((dist-1)//2):
                print("s",end='')
            print("w",end='')
            for u in range((dist-1)//2):
                print("n",end='') 
            print("w",end='')
            flag=True
            i+=(dist+1)
        else:
            print("w",end='')
            for u in range((dist-1)//2):
                print("s",end='')
            print("w",end='')
            for u in range((dist-1)//2):
                print("n",end='') 
            flag=False
            i+=(dist+1)
    x=nx
while i<len(fList)-1:
    print("w",end='')
    i+=1

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
'''