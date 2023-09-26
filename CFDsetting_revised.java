package macro;

import java.util.*;

import star.base.neo.*;
import star.segregatedflow.*;
import star.resurfacer.*;
import star.turbulence.*;
import star.flow.*;
import star.metrics.*;
import star.meshing.*;
import star.common.*;
import star.material.*;
import star.keturb.*;
import star.prismmesher.*;
import star.vis.*;
import star.motion.*;
import star.base.report.*;

import javax.swing.JOptionPane;

public class CFDsetting_revised extends StarMacro {

    public void execute() {
        execute0();
        /*
        ʹ�÷�����
        1. ���ȴ������������������޸������ҰѲ�ͬ���������Ѿ����ֺã���������Ĳ������㣩�����ҽ�������ΪSubtract��
        2. ���ϸ����費ͬ������������Ϊ��Frontwing, Rearwing, bodywork, fr, rr��
        2. ����Բ������ϵfr��rr���ֱ�Ϊǰ�ֺͺ��֣�
        3. ʣ�µ������õĻ������������
        */
    }

    public void setPhysics(Simulation SIM,double speed){

        Units units_1 = ((Units) SIM.getUnitsManager().getObject(""));
        Units units_3 = ((Units) SIM.getUnitsManager().getObject("m/s"));

        PhysicsContinuum physicsContinuum_0 = SIM.getContinuumManager().createContinuum(PhysicsContinuum.class);

            physicsContinuum_0.enable(ThreeDimensionalModel.class);
            physicsContinuum_0.enable(SingleComponentGasModel.class);
            physicsContinuum_0.enable(SegregatedFlowModel.class);
            physicsContinuum_0.enable(ConstantDensityModel.class);
            physicsContinuum_0.enable(SteadyModel.class);
            physicsContinuum_0.enable(TurbulentModel.class);
            physicsContinuum_0.enable(RansTurbulenceModel.class);
            physicsContinuum_0.enable(KEpsilonTurbulence.class);
            physicsContinuum_0.enable(RkeTwoLayerTurbModel.class);
            physicsContinuum_0.enable(KeTwoLayerAllYplusWallTreatment.class);

        VelocityProfile velocityProfile_0 = physicsContinuum_0.getInitialConditions().get(VelocityProfile.class);
        velocityProfile_0.getMethod(ConstantVectorProfileMethod.class).getQuantity().setComponents(-speed, 0.0, 0.0);
        velocityProfile_0.getMethod(ConstantVectorProfileMethod.class).getQuantity().setUnits(units_3);

        TurbulenceIntensityProfile turbulenceIntensityProfile_0 = physicsContinuum_0.getInitialConditions().get(TurbulenceIntensityProfile.class);
        turbulenceIntensityProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0.005);
        turbulenceIntensityProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_1);

        TurbulentVelocityScaleProfile turbulentVelocityScaleProfile_0 = physicsContinuum_0.getInitialConditions().get(TurbulentVelocityScaleProfile.class);
        turbulentVelocityScaleProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(speed);
        turbulentVelocityScaleProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_3);

        TurbulentViscosityRatioProfile turbulentViscosityRatioProfile_0 = physicsContinuum_0.getInitialConditions().get(TurbulentViscosityRatioProfile.class);
        turbulentViscosityRatioProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(1.0);
        turbulentViscosityRatioProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_1);
    }

    public void setBCconditions(Simulation SIM,Region REGION,LabCoordinateSystem labCoordinateSystem_0,double speed,double rotation){

        //��λ����
        Units units_1 = ((Units) SIM.getUnitsManager().getObject(""));
        Units units_3 = ((Units) SIM.getUnitsManager().getObject("m/s"));
        Units units_4 = ((Units) SIM.getUnitsManager().getObject("radian/s"));

        //�ٶȽ�������
        Boundary boundary_6 = REGION.getBoundaryManager().getBoundary("Subtract.Inlet");
        InletBoundary inletBoundary_0 = ((InletBoundary) SIM.get(ConditionTypeManager.class).get(InletBoundary.class));
        boundary_6.setBoundaryType(inletBoundary_0);

        VelocityMagnitudeProfile velocityMagnitudeProfile_0 = boundary_6.getValues().get(VelocityMagnitudeProfile.class);
        velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(speed);
        velocityMagnitudeProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_3);

        TurbulenceIntensityProfile turbulenceIntensityProfile_1 = boundary_6.getValues().get(TurbulenceIntensityProfile.class);
        turbulenceIntensityProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0.005);
        turbulenceIntensityProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_1);

        TurbulentViscosityRatioProfile turbulentViscosityRatioProfile_1 = boundary_6.getValues().get(TurbulentViscosityRatioProfile.class);
        turbulentViscosityRatioProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(1.0);
        turbulentViscosityRatioProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_1);

        //ѹ����������
        Boundary boundary_7 = REGION.getBoundaryManager().getBoundary("Subtract.Outlet");
        PressureBoundary pressureBoundary_0 = ((PressureBoundary) SIM.get(ConditionTypeManager.class).get(PressureBoundary.class));
        boundary_7.setBoundaryType(pressureBoundary_0);

        //�Գ�ƽ������
        Boundary boundary_8 = REGION.getBoundaryManager().getBoundary("Subtract.Symmetry");
        SymmetryBoundary symmetryBoundary_0 = ((SymmetryBoundary) SIM.get(ConditionTypeManager.class).get(SymmetryBoundary.class));
        boundary_8.setBoundaryType(symmetryBoundary_0);
        Boundary boundary_9 = REGION.getBoundaryManager().getBoundary("Subtract.Wall");
        boundary_9.setBoundaryType(symmetryBoundary_0);

        //��������
        Boundary boundary_10 = REGION.getBoundaryManager().getBoundary("Subtract.Ground");
        boundary_10.getConditions().get(WallSlidingOption.class).setSelected(WallSlidingOption.Type.VECTOR);

        WallRelativeVelocityProfile wallRelativeVelocityProfile_0 = boundary_10.getValues().get(WallRelativeVelocityProfile.class);
        wallRelativeVelocityProfile_0.getMethod(ConstantVectorProfileMethod.class).getQuantity().setComponents(-speed, 0.0, 0.0);
        wallRelativeVelocityProfile_0.getMethod(ConstantVectorProfileMethod.class).getQuantity().setUnits(units_3);

        //ǰ��ת������
        Boundary boundary_2 = REGION.getBoundaryManager().getBoundary("Subtract.fr");
        boundary_2.getConditions().get(WallReferenceFrameOption.class).setSelected(ReferenceFrameOption.Type.LOCAL_FRAME);
        boundary_2.getConditions().get(WallSlidingOption.class).setSelected(WallSlidingOption.Type.LOCAL_ROTATION_RATE);

        WallRelativeRotationProfile wallRelativeRotationProfile_0 = boundary_2.getValues().get(WallRelativeRotationProfile.class);
        wallRelativeRotationProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(rotation);
        wallRelativeRotationProfile_0.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_4);

        ReferenceFrame referenceFrame_0 = boundary_2.getValues().get(ReferenceFrame.class);
        CylindricalCoordinateSystem cylindricalCoordinateSystem_0 = ((CylindricalCoordinateSystem) labCoordinateSystem_0.getLocalCoordinateSystemManager().getObject("fr"));

        referenceFrame_0.setCoordinateSystem(cylindricalCoordinateSystem_0);
        referenceFrame_0.getAxisVector().setComponents(0.0, 1.0, 0.0);
        referenceFrame_0.getAxisVector().setUnits(units_1);

        //����ת������
        Boundary boundary_4 = REGION.getBoundaryManager().getBoundary("Subtract.rr");
        boundary_4.getConditions().get(WallReferenceFrameOption.class).setSelected(ReferenceFrameOption.Type.LOCAL_FRAME);
        boundary_4.getConditions().get(WallSlidingOption.class).setSelected(WallSlidingOption.Type.LOCAL_ROTATION_RATE);

        WallRelativeRotationProfile wallRelativeRotationProfile_1 = boundary_4.getValues().get(WallRelativeRotationProfile.class);
        wallRelativeRotationProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(rotation);
        wallRelativeRotationProfile_1.getMethod(ConstantScalarProfileMethod.class).getQuantity().setUnits(units_4);

        ReferenceFrame referenceFrame_1 = boundary_4.getValues().get(ReferenceFrame.class);
        CylindricalCoordinateSystem cylindricalCoordinateSystem_1 = ((CylindricalCoordinateSystem) labCoordinateSystem_0.getLocalCoordinateSystemManager().getObject("rr"));

        referenceFrame_1.setCoordinateSystem(cylindricalCoordinateSystem_1);
        referenceFrame_1.getAxisVector().setComponents(0.0, 1.0, 0.0);
        referenceFrame_1.getAxisVector().setUnits(units_1);
    }

    public void meshExecution(Simulation SIM,MeshPart meshPart_4){

        Units units_0 = SIM.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());
        Units units_1 = ((Units) SIM.getUnitsManager().getObject(""));
        Units units_2 = ((Units) SIM.getUnitsManager().getObject("mm"));

        //������
        AutoMeshOperation autoMeshOperation_0 = SIM.get(MeshOperationManager.class).createAutoMeshOperation(new StringVector(new String[] {"star.dualmesher.DualAutoMesher", "star.resurfacer.ResurfacerAutoMesher", "star.resurfacer.AutomaticSurfaceRepairAutoMesher", "star.prismmesher.PrismAutoMesher"}), new NeoObjectVector(new Object[] {meshPart_4}));

        //�����ߴ�
        autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setValue(0.1);
        autoMeshOperation_0.getDefaultValues().get(BaseSize.class).setUnits(units_0);

        //Ŀ�����ߴ�
        PartsTargetSurfaceSize partsTargetSurfaceSize_0 = autoMeshOperation_0.getDefaultValues().get(PartsTargetSurfaceSize.class);
        partsTargetSurfaceSize_0.getRelativeSizeScalar().setValue(60.0);
        partsTargetSurfaceSize_0.getRelativeSizeScalar().setUnits(units_1);

        //��С����ߴ�
        PartsMinimumSurfaceSize partsMinimumSurfaceSize_0 = autoMeshOperation_0.getDefaultValues().get(PartsMinimumSurfaceSize.class);
        partsMinimumSurfaceSize_0.getRelativeSizeScalar().setValue(2.0);
        partsMinimumSurfaceSize_0.getRelativeSizeScalar().setUnits(units_1);

        //��������
        SurfaceCurvature surfaceCurvature_0 = autoMeshOperation_0.getDefaultValues().get(SurfaceCurvature.class);
        surfaceCurvature_0.setNumPointsAroundCircle(400.0);

        //������������
        SurfaceGrowthRate surfaceGrowthRate_0 = autoMeshOperation_0.getDefaultValues().get(SurfaceGrowthRate.class);
        surfaceGrowthRate_0.setGrowthRateOption(SurfaceGrowthRate.GrowthRateOption.USER_SPECIFIED);
        surfaceGrowthRate_0.getGrowthRateScalar().setValue(1.2);
        surfaceGrowthRate_0.getGrowthRateScalar().setUnits(units_1);

        //��������
        NumPrismLayers numPrismLayers_0 = autoMeshOperation_0.getDefaultValues().get(NumPrismLayers.class);
        IntegerValue integerValue_0 = numPrismLayers_0.getNumLayersValue();
        integerValue_0.getQuantity().setValue(6.0);

        //����������
        PrismLayerStretching prismLayerStretching_0 = autoMeshOperation_0.getDefaultValues().get(PrismLayerStretching.class);
        prismLayerStretching_0.getStretchingQuantity().setValue(1.2);
        prismLayerStretching_0.getStretchingQuantity().setUnits(units_1);

        //��������
        PrismThickness prismThickness_0 = autoMeshOperation_0.getDefaultValues().get(PrismThickness.class);
        prismThickness_0.getRelativeOrAbsoluteOption().setSelected(RelativeOrAbsoluteOption.Type.ABSOLUTE);
        ((ScalarPhysicalQuantity) prismThickness_0.getAbsoluteSizeValue()).setValue(5.23);
        ((ScalarPhysicalQuantity) prismThickness_0.getAbsoluteSizeValue()).setUnits(units_2);
    }

    private String createGroup(Region REGION){
        //�½���
        REGION.getBoundaryManager().getGroupsManager().createGroup("car");

        //�ѱ߽�Ž�����
        Boundary boundary_1 = REGION.getBoundaryManager().getBoundary("Subtract.bodywork");
        Boundary boundary_2 = REGION.getBoundaryManager().getBoundary("Subtract.fr");
        Boundary boundary_3 = REGION.getBoundaryManager().getBoundary("Subtract.Frontwing");
        Boundary boundary_4 = REGION.getBoundaryManager().getBoundary("Subtract.rr");
        Boundary boundary_5 = REGION.getBoundaryManager().getBoundary("Subtract.Rearwing");

        ((ClientServerObjectGroup) REGION.getBoundaryManager().getGroupsManager().getObject("car")).getGroupsManager().groupObjects("car", new NeoObjectVector(new Object[] {boundary_1, boundary_2, boundary_3, boundary_4, boundary_5}), true);
        
        return "car";
    }

    public void assign2Region(Simulation SIM,Region REGION,MeshPart meshPart_4){
        //��������
        Boundary boundary_0 = REGION.getBoundaryManager().getBoundary("Default");
        REGION.getBoundaryManager().removeBoundaries(new NeoObjectVector(new Object[] {boundary_0}));

        //�м�FeatureCurve��֪����ɶ��
        FeatureCurve featureCurve_0 = ((FeatureCurve) REGION.getFeatureCurveManager().getObject("Default"));
        REGION.getFeatureCurveManager().removeObjects(featureCurve_0);
        FeatureCurve featureCurve_1 = REGION.getFeatureCurveManager().createEmptyFeatureCurveWithName("Characteristic Curves");

        SIM.getRegionManager().newRegionsFromParts(new NeoObjectVector(new Object[] {meshPart_4}), "OneRegion", REGION, "OneBoundaryPerPartSurface", null, "OneFeatureCurve", featureCurve_1, RegionManager.CreateInterfaceMode.BOUNDARY, "OneEdgeBoundaryPerPart", null);
    }

    public void createReport(Simulation SIM,Region REGION,double speed,double density){

        Units units_0 = SIM.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());
        Units units_1 = ((Units) SIM.getUnitsManager().getObject(""));
        Units units_2 = ((Units) SIM.getUnitsManager().getObject("mm"));
        Units units_3 = ((Units) SIM.getUnitsManager().getObject("m/s"));
        Units units_4 = ((Units) SIM.getUnitsManager().getObject("radian/s"));
        Units units_5 = ((Units) SIM.getUnitsManager().getObject("m"));
        Units units_6 = ((Units) SIM.getUnitsManager().getObject("kg/m^3"));
        Units units_7 = ((Units) SIM.getUnitsManager().getObject("m^2"));
        Boundary boundary_11 = REGION.getBoundaryManager().getBoundary("Subtract.bodywork");
        Boundary boundary_12 = REGION.getBoundaryManager().getBoundary("Subtract.fr");
        Boundary boundary_13 = REGION.getBoundaryManager().getBoundary("Subtract.Frontwing");
        Boundary boundary_14 = REGION.getBoundaryManager().getBoundary("Subtract.Rearwing");
        Boundary boundary_15 = REGION.getBoundaryManager().getBoundary("Subtract.rr");

        //�½�����
        ForceReport forceReport_0 = SIM.getReportManager().createReport(ForceReport.class);
        forceReport_0.getParts().setQuery(null);
        forceReport_0.getParts().setObjects(boundary_11, boundary_12, boundary_13, boundary_14, boundary_15);
        forceReport_0.getDirection().setComponents(-1.0, 0.0, 0.0);
        forceReport_0.getDirection().setUnits(units_1);
        forceReport_0.setPresentationName("drag");

        ForceReport forceReport_1 = SIM.getReportManager().createReport(ForceReport.class);
        forceReport_1.getParts().setQuery(null);
        forceReport_1.getParts().setObjects(boundary_11, boundary_12, boundary_13, boundary_14, boundary_15);
        forceReport_1.getDirection().setComponents(0.0, 0.0, 1.0);
        forceReport_1.getDirection().setUnits(units_1);
        forceReport_1.setPresentationName("lift");

        ForceCoefficientReport forceCoefficientReport_0 = SIM.getReportManager().createReport(ForceCoefficientReport.class);
        forceCoefficientReport_0.getParts().setQuery(null);
        forceCoefficientReport_0.getParts().setObjects(boundary_11, boundary_12, boundary_13, boundary_14, boundary_15);
        forceCoefficientReport_0.getDirection().setComponents(-1.0, 0.0, 0.0);
        forceCoefficientReport_0.getDirection().setUnits(units_1);
        forceCoefficientReport_0.getReferenceDensity().setValue(density);     
        forceCoefficientReport_0.getReferenceDensity().setUnits(units_6);
        forceCoefficientReport_0.getReferenceVelocity().setValue(speed);
        forceCoefficientReport_0.getReferenceVelocity().setUnits(units_3);
        forceCoefficientReport_0.getReferenceArea().setValue(4.0);
        forceCoefficientReport_0.getReferenceArea().setUnits(units_7);
        forceCoefficientReport_0.setPresentationName("Cd");

        ForceCoefficientReport forceCoefficientReport_1 = SIM.getReportManager().createReport(ForceCoefficientReport.class);
        forceCoefficientReport_1.getParts().setQuery(null);
        forceCoefficientReport_1.getParts().setObjects(boundary_11, boundary_12, boundary_13, boundary_14, boundary_15);
        forceCoefficientReport_1.getDirection().setComponents(0.0, 0.0, 1.0);
        forceCoefficientReport_1.getDirection().setUnits(units_1);
        forceCoefficientReport_1.getReferenceDensity().setValue(density);
        forceCoefficientReport_1.getReferenceDensity().setUnits(units_6);
        forceCoefficientReport_1.getReferenceVelocity().setValue(speed);
        forceCoefficientReport_1.getReferenceVelocity().setUnits(units_3);
        forceCoefficientReport_1.getReferenceArea().setValue(4.0);
        forceCoefficientReport_1.getReferenceArea().setUnits(units_7);
        forceCoefficientReport_1.setPresentationName("Cl");

        ForceReport forceReport_2 = SIM.getReportManager().createReport(ForceReport.class);
        forceReport_2.getParts().setQuery(null);
        forceReport_2.getParts().setObjects(boundary_11);
        forceReport_2.getDirection().setComponents(0.0, 0.0, 1.0);
        forceReport_2.getDirection().setUnits(units_1);
        forceReport_2.setPresentationName("bodywork");

        ForceReport forceReport_3 = SIM.getReportManager().createReport(ForceReport.class);
        forceReport_3.getDirection().setComponents(0.0, 0.0, 1.0);
        forceReport_3.getDirection().setUnits(units_1);
        forceReport_3.getParts().setQuery(null);
        forceReport_3.getParts().setObjects(boundary_13);
        forceReport_3.setPresentationName("Frontwing");

        ForceReport forceReport_4 = SIM.getReportManager().createReport(ForceReport.class);
        forceReport_4.getDirection().setComponents(0.0, 0.0, 1.0);
        forceReport_4.getDirection().setUnits(units_1);
        forceReport_4.getParts().setQuery(null);
        forceReport_4.getParts().setObjects(boundary_14);
        forceReport_4.setPresentationName("Rearwing");

        FrontalAreaReport frontalAreaReport_0 = SIM.getReportManager().createReport(FrontalAreaReport.class);
        frontalAreaReport_0.getParts().setQuery(null);
        frontalAreaReport_0.getParts().setObjects(boundary_11, boundary_12, boundary_13, boundary_14, boundary_15);   
        frontalAreaReport_0.getNormalCoordinate().setCoordinate(units_5, units_5, units_5, new DoubleVector(new double[] {-1.0, 0.0, 0.0}));
        frontalAreaReport_0.setPresentationName("Ad");
        frontalAreaReport_0.printReport();

        FrontalAreaReport frontalAreaReport_1 = SIM.getReportManager().createReport(FrontalAreaReport.class);
        frontalAreaReport_1.getParts().setQuery(null);
        frontalAreaReport_1.getParts().setObjects(boundary_11, boundary_12, boundary_13, boundary_14, boundary_15);
        frontalAreaReport_1.getViewUpCoordinate().setCoordinate(units_5, units_5, units_5, new DoubleVector(new double[] {1.0, 0.0, 0.0}));
        frontalAreaReport_1.setPresentationName("Al");
        frontalAreaReport_1.printReport();

        //����������
        ReportMonitor reportMonitor_0 = forceReport_2.createMonitor();
        ReportMonitor reportMonitor_1 = forceCoefficientReport_0.createMonitor();
        ReportMonitor reportMonitor_2 = forceCoefficientReport_1.createMonitor();
        ReportMonitor reportMonitor_3 = forceReport_0.createMonitor();
        ReportMonitor reportMonitor_4 = forceReport_3.createMonitor();
        ReportMonitor reportMonitor_5 = forceReport_1.createMonitor();
        ReportMonitor reportMonitor_6 = forceReport_4.createMonitor();

        //������ͼ
        MonitorPlot monitorPlot_0 = SIM.getPlotManager().createPlot(MonitorPlot.class);
        monitorPlot_0.open();

        PlotUpdate plotUpdate_0 = monitorPlot_0.getPlotUpdate();
        HardcopyProperties hardcopyProperties_2 = plotUpdate_0.getHardcopyProperties();

        monitorPlot_0.setPresentationName("Force");
        monitorPlot_0.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {reportMonitor_0}));
        monitorPlot_0.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {reportMonitor_3}));
        monitorPlot_0.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {reportMonitor_4}));
        monitorPlot_0.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {reportMonitor_5}));
        monitorPlot_0.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {reportMonitor_6}));

        MonitorPlot monitorPlot_1 = SIM.getPlotManager().createPlot(MonitorPlot.class);
        monitorPlot_1.open();

        PlotUpdate plotUpdate_1 = monitorPlot_1.getPlotUpdate();
        HardcopyProperties hardcopyProperties_3 = plotUpdate_1.getHardcopyProperties();

        monitorPlot_1.setPresentationName("Force Coefficient");
        monitorPlot_1.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {reportMonitor_1}));
        monitorPlot_1.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {reportMonitor_2}));
    }

    public void setStopCriterion(Simulation SIM,int steps){
        StepStoppingCriterion stepStoppingCriterion_0 = ((StepStoppingCriterion) SIM.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
        IntegerValue integerValue_1 = stepStoppingCriterion_0.getMaximumNumberStepsObject();
        integerValue_1.getQuantity().setValue(steps);
    }

    private void execute0() {
        //��ȡ��ǰģ���ļ���Ϣ
        Simulation SIM = getActiveSimulation();

        //����ʹ����
        int n = JOptionPane.showConfirmDialog(null, "��ȷ�ϣ�"+"\n"+"1. �Ѵ������������������޸������ҰѲ�ͬ���������Ѿ����ֺã���������Ĳ������㣩�����ҽ�������ΪSubtract��"+"\n"+"2.���ϸ����費ͬ������������Ϊ��Frontwing, Rearwing, bodywork, fr, rr, Inlet, Outlet, Symmetry, Wall��"+"\n"+"3.����Բ������ϵfr��rr���ֱ�Ϊǰ�ֺͺ��֣���", "ʹ�÷���",JOptionPane.YES_NO_OPTION); //����ֵΪ0��1
        if(n==1){
            return;
        }

        Object[] choices = {"�½�","����"};
        if(JOptionPane.showOptionDialog(null, "��ģ���ļ�Ϊ�½������ã�", "ģ���ļ�����",JOptionPane.YES_NO_CANCEL_OPTION ,JOptionPane.QUESTION_MESSAGE,null, choices, choices[0])==0){
            UserUnits userUnits_0 = SIM.getUnitsManager().createUnits("units");
            userUnits_0.setDimensions(Dimensions.Builder().velocity(1).build());
            userUnits_0.setPresentationName("m/s");

            UserUnits userUnits_1 = SIM.getUnitsManager().createUnits("units");
            userUnits_1.setPresentationName("kg/m^3");
            userUnits_1.setDimensions(Dimensions.Builder().mass(1).length(-3).build());

            UserUnits userUnits_2 = SIM.getUnitsManager().createUnits("units");
            userUnits_2.setDimensions(Dimensions.Builder().angularVelocity(1).build());
            userUnits_2.setPresentationName("radian/s");
        }

        //promptUserForInput()ʹ�û�������Ϣ
        double speed=promptUserForInput("Velocity(m/s)",22);
        double WheelRadius=promptUserForInput("���ְ뾶(mm)",235);
        double rotation=speed/WheelRadius*1000;
        double density=promptUserForInput("�ܶ�(kg/m^3)",1.18415);
        int steps=promptUserForInput("ֹͣ��׼",800);

        


        //�õ��ĵ�λ����
        Units units_0 = SIM.getUnitsManager().getPreferredUnits(Dimensions.Builder().length(1).build());
        Units units_1 = ((Units) SIM.getUnitsManager().getObject(""));
        Units units_2 = ((Units) SIM.getUnitsManager().getObject("mm"));
        Units units_3 = ((Units) SIM.getUnitsManager().getObject("m/s"));
        Units units_4 = ((Units) SIM.getUnitsManager().getObject("radian/s"));
        Units units_5 = ((Units) SIM.getUnitsManager().getObject("m"));
        Units units_6 = ((Units) SIM.getUnitsManager().getObject("kg/m^3"));
        Units units_7 = ((Units) SIM.getUnitsManager().getObject("m^2"));
        
        //ȫ������ϵ����
        LabCoordinateSystem labCoordinateSystem_0 = SIM.getCoordinateSystemManager().getLabCoordinateSystem();

        /*
        //����������
        MeshPartFactory meshPartFactory_0 = SIM.get(MeshPartFactory.class);
        
        SimpleBlockPart simpleBlockPart_0 = meshPartFactory_0.createNewBlockPart(SIM.get(SimulationPartManager.class));

        simpleBlockPart_0.setDoNotRetessellate(true);
        simpleBlockPart_0.setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_0.getCorner1().setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_0.getCorner1().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {-15.0, -5.0, 0.0}));
        simpleBlockPart_0.getCorner2().setCoordinateSystem(labCoordinateSystem_0);
        simpleBlockPart_0.getCorner2().setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {10.0, 0.0, 5.0}));
        simpleBlockPart_0.rebuildSimpleShapePart();
        simpleBlockPart_0.setDoNotRetessellate(false);


        //��������
        MeshActionManager meshActionManager_0 = SIM.get(MeshActionManager.class);
        MeshPart meshPart_3 = ((MeshPart) SIM.get(SimulationPartManager.class).getPart("Main"));
        MeshPart meshPart_4 = meshActionManager_0.subtractParts(new NeoObjectVector(new Object[] {simpleBlockPart_0, meshPart_3}), simpleBlockPart_0, "\u79BB\u6563");


        //���տ���зָ�
        PartSurface partSurface_60 = ((PartSurface) meshPart_4.getPartSurfaceManager().getPartSurface("Block Surface"));

        meshPart_4.splitPartSurfaceByPatch(partSurface_60, new IntVector(new int[] {8512}), "Symmetry");
        meshPart_4.splitPartSurfaceByPatch(partSurface_60, new IntVector(new int[] {8514}), "Inlet");
        meshPart_4.splitPartSurfaceByPatch(partSurface_60, new IntVector(new int[] {8511}), "Outlet");
        meshPart_4.splitPartSurfaceByPatch(partSurface_60, new IntVector(new int[] {8513}), "Ground");
        partSurface_60.setPresentationName("Wall");*/
        

        //������������
        MeshPart meshPart_4 = ((MeshPart) SIM.get(SimulationPartManager.class).getPart("Subtract"));

        //������
        meshExecution(SIM,meshPart_4);

        //��������
        Region REGION = SIM.getRegionManager().createEmptyRegion();
        REGION.setPresentationName("Region");
        assign2Region(SIM,REGION,meshPart_4);

        //���½����ı߽���࣬����Ϊcar
        String GroupName=createGroup(REGION);

        //��������
        setPhysics(SIM,speed);
        
        //�߽���������
        setBCconditions(SIM,REGION,labCoordinateSystem_0,speed,rotation);

        //��������ͻ�ͼ
        createReport(SIM,REGION,speed,density);

        //ֹͣ��׼
        setStopCriterion(SIM,steps);

    }


}
