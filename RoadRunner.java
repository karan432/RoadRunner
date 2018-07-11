
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class RoadRunner extends Applet implements Runnable,KeyListener
{
	private Image dbImage;
	private Graphics dbg;
	boolean runner; 
	boolean pause;
	boolean godmode;
	boolean started;
	boolean crashed;
	int i,delay,frameh,framew,timer,tdelay,tmax;//general
	float speed;
	int count,tcount;
	int base;
	int b;
	Color white,grey,red; //custom color
	int xd,yd,wd,hd; //x & y for divider
	int xs,ys,tys;
	int xb,yb[],wb,hb,nb,thb;//x & y for break
	int xc,yc,wc,hc,mov,tmov;
	int lmax,rmax;//x & y for car
	Image mycar;
	int xo[],yo[],wo,ho,carc[],tyo[];//opponent
	int no,mind,maxd,nco;
	boolean onscreen[];
	String s[];
	Image ocar[];
	Image crash;
	Image strip;
	Random r;
	Thread move=null;
	Font f1,f2;
	public void init()
	{
		runner=true;
		started=false;
		crashed=false;
		godmode=false;
		pause=false;
		white =new Color(225,225,225);
		grey=new Color(110,110,110);
		red=new Color(255,0,0);
		setBackground(grey);
		frameh=650;
		framew=520;
		base=0;
		b=0;
		speed=1;
		delay=6;
		tdelay=delay;
		timer=0;
		tmax=150;
		count=0;
		tcount=6;
		
		xd=250;  yd=0; wd=20;  hd=650; //divider
		tys=0;
		nb=5;  wb=wd; hb=25;    //break
		xb=xd;
		yb=new int[nb];
		for(i=0;i<nb;++i)
		{	yb[i]=(i*((hd-hb*nb)/nb))+(hb*i);		}//break
		xc=300; yc=450; wc=90; hc=140; lmax=130; 					rmax=390;mov=4;tmov=mov;//car
		xs=lmax-20;ys=yc-50;
		addKeyListener(this);//key handling 
		r=new Random(System.currentTimeMillis());
		no=250; wo=90; ho=140;    //opponent
		mind=400;
		maxd=750;
		yo=new int [no];
		tyo=new int[no];
		xo=new int[no];
		carc=new int[no];
		onscreen=new boolean[no];
		yo[0]=base-ho;
		xo[0]=140;
		for(i=1;i<no;++i)
		{
			onscreen[i]=false;
			if((r.nextInt(10-1)+1)<=5)
				xo[i]=140;
			else
				xo[i]=290;
			if(xo[i]==xo[i-1])
				mind=230;
			else
				mind=400;
			yo[i]=yo[i-1]-(r.nextInt(maxd-mind)+mind);
			carc[i]=r.nextInt(6-1)+1;
			
		}//opponent
		mycar=getImage(getDocumentBase(),"mycar.png");
		crash=getImage(getDocumentBase(),"crash.png");
		strip=getImage(getDocumentBase(),"strip.png");
		ocar=new Image[6];
		s=new String[6];
		s[0]="ocar1.jpe";
		s[1]="ocar2.jpg";
		s[2]="ocar3.jpe";
		s[3]="ocar4.jpe";
		s[4]="ocar5.jpe";
		s[5]="ocar6.jpe";
		
		for(i=0;i<6;++i)
		{
			ocar[i]=getImage(getDocumentBase(),s[i]);
		}
		nco=0;
		f1=new Font("SansSerif",Font.BOLD,26);
		f2=new Font("Constantia",Font.BOLD,20);
		setFont(f1);
	}//init
	public void start()
	{	move=new Thread(this);
		//move.setPriority(Thread.NORM_PRIORITY);
		move.start();	}//start
	public void run()
	{	
		
		while(true)
		{
			try
			{
				if(!godmode)
					checkcrashed();
				
				base+=b;
				timer=timer+delay;
				if(timer>tmax)
				{
				for(i=0;i<nb;++i)//to animate break
				{
					yb[i]=yb[i]+thb;
					if(yb[i]>hd)
						yb[i]=0;
					
				}
				timer=0;
				}
				if(ys<frameh)
					ys=ys+tys;
				for(i=nco;i<nco+10;++i)
				{
						tyo[i]=yo[i]+base;
					if(!onscreen[i])
						{
						if(tyo[i]>(-90))
							onscreen[i]=true;
						}
					else
						if(tyo[i]>frameh)
						{
							count++;
							if(count%tcount==0)
							{
								tcount=count+tcount+1;
								if(delay>2)
									{
										delay--;
										tmax-=10;
										if(delay%2==0)
											mov+=2;
									}
								
							}
							
							nco++;
							onscreen[i]=false;
						}
					
				}
				
				repaint();
				if(crashed)
				{
					repaint();
					Thread.sleep(2000);
					runner=false;
					setFont(f2);
					repaint();
					break;
				}
				Thread.sleep(delay);
			}
			catch(InterruptedException e)
			{
				System.out.print("exception occured");
			}
			
				
		}
	}
	public void keyPressed(KeyEvent ke) 
	{
		int key=ke.getKeyCode();
		if(!crashed)
			switch(key)
				{
				case KeyEvent.VK_LEFT:
					if(!pause)
					{
					showStatus("left arrow");
					if( xc > lmax)
						xc=(int)(xc-(mov));	
					}
					break;
				case KeyEvent.VK_RIGHT:
					if(!pause)
					{
					showStatus("right arrow");
					if((xc+wc) < rmax)
						xc=(int) (xc+(mov));
					}
					break;
				
				case KeyEvent.VK_SPACE:
					godmode=!godmode;
					break;
				case KeyEvent.VK_UP:
					if(!started)
						{
							started=true;
							thb=hb;
							tys=1;
							b=1;
							
						}
					break;
				case KeyEvent.VK_ESCAPE:
					if(started)
						if(pause==false)
						{
							pause=true;
							thb=0;
							tys=0;
							b=0;						
						}
						else
							if(started==true)
							{
								pause=false;
								thb=hb;
								tys=1;
								b=1;
							}
					
				}
		repaint();
			
	}
	public void keyReleased(KeyEvent ke) 
	{	}
	public void keyTyped(KeyEvent ke) 
	{
		if(ke.getKeyCode()==KeyEvent.VK_UP)
		{
			showStatus("speed up");
			if(delay>2)
				delay--;
		}
	}
	
	
	public void checkcrashed()
	{	for(i=nco;i<(nco+10);++i)
		{
			if((xo[i]<=xc &&  xc<=xo[i]+wo) || (xo[i]>=xc && 					xo[i]<=xc+wc))
				if((yc<=tyo[i] && tyo[i]<=(yc+hc)) || (yc>=tyo[i] 					&& yc<=(tyo[i]+ho)))
					{
						crashed=true;
					}
		} 
	}
	public void stop()
	{	move=null;	}//stop
	public void destroy()
	{	}//destroy
	public void update (Graphics g)
	{	 
	dbImage = createImage (this.getSize().width, 							this.getSize().height);
	dbg = dbImage.getGraphics ();
	// initialize buffer
	if (dbImage == null)
	{}
	// clear screen in background
	dbg.setColor (getBackground ());
	dbg.fillRect (0, 0, this.getSize().width, this.getSize().height);
	// draw elements in background
	dbg.setColor (getForeground());
	paint (dbg);
	// draw image on the screen
	g.drawImage (dbImage, 0, 0, this);
	}
	public void paint(Graphics g)
	{
		if(runner)
		{
			g.setColor(Color.YELLOW);
			g.fillRect(lmax-40,0,wd,frameh);
			g.fillRect(rmax+20,0,wd,frameh);
			g.setColor(Color.green);
			g.fillRect(0,0,lmax-45,frameh);
			g.fillRect(rmax+45,0,(framew-(rmax+45)),frameh);
			
			g.setColor(white);
			g.fillRect(xd,yd,wd,hd);
			g.setColor(grey);
			for(i=0;i<nb;++i)
			{
				g.fillRect(xb, yb[i],wb,hb);
			}
			for(i=nco;i<nco+10;++i)
			{
				g.drawImage(ocar[carc[i]],xo[i],tyo[i],this);
			}
			if(ys<frameh)
				g.drawImage(strip,xs,ys,this);
			g.setColor(white);
			g.drawImage(mycar,xc, yc,this );
			if(crashed)
			{
				g.drawImage(crash,framew/2-65,frameh/2-50,this);
				g.setColor(red);
				g.drawString("CRASHED!", framew/2-40, 									frameh/2+10);
			}
			
			g.drawString(""+count+/*"    										"+delay+*/"",rmax+70,frameh-100);
			if(pause)
			{
				g.setColor(Color.BLACK);
				g.drawString("PAUSE", framew/2-35, 30);
			}
			if(!started)
			{
				g.setColor(Color.BLACK);
				g.drawString("Pess UP KEY to begin",framew/2-								140,frameh/2-100);
			}
		}//if
		else
		{	setBackground(grey);
			g.setColor(Color.RED);
			g.drawString("GAME OVER!!",200,325);
			g.setColor(Color.green);
			g.drawString("your score: "+count+"",200 ,350); }//else
	}//paint
}
