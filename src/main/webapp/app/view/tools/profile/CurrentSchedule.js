Ext.define('Ssp.view.tools.profile.CurrentSchedule', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.profilecurrentschedule',
    mixins: ['Deft.mixin.Injectable', 'Deft.mixin.Controllable'],
    width: '100%',
    height: '100%',
    autoScroll: true,
	title: 'Current Schedule',
    inject: {
        //store: 'currentScheduleStore'
    },
    initComponent: function(){
        var me = this;
        Ext.applyIf(me, {
            //store: me.store,
            xtype: 'gridcolumn',
            columns: [{
                dataIndex: 'course',
                text: 'Course',
				flex: 1
            }, {
                dataIndex: 'creditHrs',
                text: 'Cr Hrs',
				flex: 1
            }, {
            
                dataIndex: 'courseTitle',
                text: 'Course Title',
				flex: 1
            },
			{
                dataIndex: 'term',
                text: 'Term',
                flex: 1
            }, {
            
                dataIndex: 'instructor',
                text: 'Instructor',
                flex: 1
            }
			],
            viewConfig: {}
        });
        
        me.callParent(arguments);
    }
});